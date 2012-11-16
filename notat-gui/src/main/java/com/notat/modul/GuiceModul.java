package com.notat.modul;

import com.drivesync.dto.LocalDateTimeDeserializer;
import com.drivesync.server.StartDriveSync;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.notat.klient.NotatKlient;
import org.joda.time.LocalDateTime;

public class GuiceModul extends AbstractModule {

    @Override
    protected void configure() {
        bind(StartDriveSync.class);
        bind(NotatKlient.class);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        Gson gson = gsonBuilder.create();
        bind(Gson.class).toInstance(gson);
    }
}
