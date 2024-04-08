package org.residentme.workorder.util;

import com.google.gson.Gson;

public class JsonCovert {
	
	public static String convert2Str(Object obj) {
		Gson gson = new Gson();
		try {
            return gson.toJson(obj);
        } catch (Exception e) {
            return e.getMessage();
        }
	}

}
