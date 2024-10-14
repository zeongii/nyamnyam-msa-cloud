package kr.admin.absent;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtill {

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getListMapFromJsonArray(JSONArray jsonArray) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object obj : jsonArray) {
            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObject = (JSONObject) obj;
            for (Object key : jsonObject.keySet()) {
                map.put(key.toString(), jsonObject.get(key));
            }
            list.add(map);
        }
        return list;
    }
}