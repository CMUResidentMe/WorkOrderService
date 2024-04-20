package org.residentme.workorder.util;

import org.residentme.workorder.dto.RmNotification;

import com.google.gson.Gson;

public class JsonUtil {

    public static String convert2Str(RmNotification obj) {
        Gson gson = new Gson();
        try {
            return gson.toJson(obj);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
