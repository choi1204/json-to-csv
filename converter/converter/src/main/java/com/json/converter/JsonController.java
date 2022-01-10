package com.json.converter;

import org.apache.coyote.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsonController {

    @GetMapping("/test")
    public ResponseEntity<String> parseJson(@RequestBody String json) {
        JSONArray jsonArray = new JSONArray(json);
        String csv = JSONUtil.parseCSV(jsonArray);

        return ResponseEntity.ok(csv);
    }
}
