package com.notat.server.module;

import com.notat.server.dto.LocalDateTimeDeserializer;
import com.notat.server.servlet.NotatServlet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import org.joda.time.LocalDateTime;

public class GuiceServlet extends GuiceServletContextListener {

    private Injector injector;

    @Override
    protected Injector getInjector() {
        injector = Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/notat").with(NotatServlet.class);
                serve("/notat/*").with(NotatServlet.class);

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
                Gson gson = gsonBuilder.create();
                bind(Gson.class).toInstance(gson);
            }
        });
        return injector;
    }

    public Injector getProduksjonInjector() {
        return injector;
    }
}