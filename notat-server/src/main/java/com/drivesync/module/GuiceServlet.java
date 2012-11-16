package com.drivesync.module;

import com.drivesync.dto.LocalDateTimeDeserializer;
import com.drivesync.servlet.NotatServlet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import org.joda.time.LocalDateTime;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class GuiceServlet extends GuiceServletContextListener {

    private Injector injector;

    @Override
    protected Injector getInjector() {
        injector = Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                try {
                    serve("/notat").with(NotatServlet.class);
                    serve("/notat/").with(NotatServlet.class);

                    InitialContext ct = new InitialContext();
                    DataSource ds = (DataSource) ct.lookup("jdbc/notat");
                    bind(DataSource.class).toInstance(ds);

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
                    Gson gson = gsonBuilder.create();
                    bind(Gson.class).toInstance(gson);
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        return injector;
    }

    public Injector getProduksjonInjector() {
        return injector;
    }
}