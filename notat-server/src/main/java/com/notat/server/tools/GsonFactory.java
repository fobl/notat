package com.notat.server.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.notat.server.dto.LocalDateTimeDeserializer;
import org.joda.time.LocalDateTime;

public class GsonFactory {
    public static Gson gson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        return gsonBuilder.create();
    }
}
