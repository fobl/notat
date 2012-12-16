package com.notat.server.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.LocalDateTime;

public abstract class JsonDto {

    private static Gson gson;

    public JsonDto(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        gson = gsonBuilder.create();
    }

    public String toJson(){
        return gson.toJson(this);
    }

    protected static Gson gson(){
        return gson;
    }
}
