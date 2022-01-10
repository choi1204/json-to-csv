package com.json.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {

    public static String parseCSV(JSONArray jsonArray) {

        List<Map<String, String>> jsonList = getJsonList(jsonArray);
        String csv = getCSV(jsonList);

        return csv;
    }

    public static List<Map<String, String>> getJsonList(JSONArray jsonArray) {

        List<Map<String, String>> jsonList = new ArrayList<>();
        addList(jsonList);
        parser(jsonArray, jsonList, "");
        setNull(jsonList);

        return jsonList;
    }

    private static String getCSV(List<Map<String, String>> jsonList) {

        Set<String> keys = jsonList.get(0).keySet();
        String header = getHeader(keys);
        StringBuilder body = new StringBuilder();
        int i = 0;

        for (Map<String, String> json : jsonList) {
            StringBuilder line = new StringBuilder();
            if(i++ != 0) {
                body.append("\n");
            }
            Set<String> keySet = jsonList.get(0).keySet();

            //id를 맨앞으로 나타내기 위한 작업.
            for (String key : keySet) {
                if("id".equals(key)){
                    line = new StringBuilder(json.get(key) + "," + line);
                } else {
                    line.append(json.get(key) + ",");
                }
            }

            line.deleteCharAt(line.length() - 1);
            body.append(line);
        }

        return header + body;
    }

    //가장 앞에 와야하는 헤더확인.
    private static boolean checkFirstHeader(String key) {

        if ("".equals(key)) {
            return true;
        }

        return false;
    }

    private static String getHeader(Set<String> keys) {
        StringBuilder s = new StringBuilder();
        for (String key : keys) {
            if (checkFirstHeader(key)){
                s = new StringBuilder(key + "," + s);
            } else {
                s.append(key + ",");
            }
        }
        s.deleteCharAt(s.length() - 1);
        s.append("\n");
        return s.toString();
    }

    private static void setNull(List<Map<String, String>> list) {

        int index = 0;
        for(Map<String,String> json : list) {
            Set<String> keys = list.get(0).keySet();
            Map<String, String> map = new HashMap<>();

            for (String key : keys) {
                if (!json.containsKey(key)) {
                    map.put(key,"");
                } else {
                    map.put(key, json.get(key));
                }
            }
            list.set(index, map);
            index++;
        }
    }

    //제거할 Prefix 확인.
    private static boolean checkPrefix(String prefix) {
        if ("".equals(prefix)) {
            return true;
        }

        return false;
    }

    private static String addPrefix(String prefix, String key) {

        if(checkPrefix(prefix)) {
            return key;
        } else {
            return prefix + "_" + key;
        }
    }

    private static void parser(JSONObject jsonObject, List<Map<String,String>> jsonList, String prefix) {

        Set<String> keys = jsonObject.keySet();
        Map<String, String> json = jsonList.get(jsonList.size() - 1);

        for (String keyName : keys) {
            if (!checkHeader(keyName)) {
                continue;
            }
            try {
                Object body = jsonObject.get(keyName);
                String name = addPrefix(prefix, keyName);
                if (body instanceof JSONObject) {
                    json.put(name, "");
                    parser((JSONObject) body, jsonList, name);
                } else if (body instanceof JSONArray) {
                    json.put(name, "");
                    parser((JSONArray) body, jsonList, name);
                } else {
                    String content = body.toString();
                    if(content != null) {
                        json.put(name, content);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static void parser(JSONArray jsonArray, List<Map<String, String>> jsonList, String prefix) {

        for (int i = 0; i < jsonArray.length(); i++) {
            if(i != 0) {
                addList(jsonList);
            }
            try {
                Object body = jsonArray.get(i);
                if (body instanceof JSONObject) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    parser(jsonObject, jsonList, prefix);
                } else if(body instanceof JSONArray) {
                    JSONArray subArray = jsonArray.getJSONArray(i);
                    parser(subArray, jsonList, prefix);
                } else {
                    throw new RuntimeException("JSONArray 확인");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static void addList(List<Map<String, String>> jsonList) {
        HashMap<String, String> copyJson = new HashMap<>();
        jsonList.add(copyJson);
    }

    //포함안할 헤더 체크.
    private static boolean checkHeader(String s) {

        if ("".equals(s))
            return false;

        return true;
    }
}