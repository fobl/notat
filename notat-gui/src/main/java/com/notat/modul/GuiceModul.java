package com.notat.modul;

import com.drivesync.dto.LocalDateTimeDeserializer;
import com.drivesync.module.GuiceServlet;
import com.drivesync.server.StartDriveSync;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.notat.klient.NotatKlient;
import org.joda.time.LocalDateTime;

public class GuiceModul extends AbstractModule {

    @Override
    protected void configure() {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
            Gson gson = gsonBuilder.create();
            bind(Gson.class).toInstance(gson);

            StartDriveSync start = new StartDriveSync(new GuiceServlet());
            bind(NotatKlient.class).toInstance(new NotatKlient(start, gson));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
