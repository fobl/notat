package com.notat.gui.modul;

import com.notat.server.dto.LocalDateTimeDeserializer;
import com.notat.server.module.GuiceServlet;
import com.notat.server.StartDriveSync;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.notat.gui.klient.NotatKlient;
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
