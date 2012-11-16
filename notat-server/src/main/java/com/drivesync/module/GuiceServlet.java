package com.drivesync.module;

import com.drivesync.dto.LocalDateTimeDeserializer;
import com.drivesync.servlet.NotatServlet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import org.apache.commons.dbcp.BasicDataSource;
import org.joda.time.LocalDateTime;

import javax.sql.DataSource;

public class GuiceServlet extends GuiceServletContextListener {

    private Injector injector;

    @Override
    protected Injector getInjector() {
        injector = Guice.createInjector(new ServletModule(){
            @Override
            protected void configureServlets() {
                serve("/notat").with(NotatServlet.class);
                serve("/notat/").with(NotatServlet.class);

                BasicDataSource ds = new BasicDataSource();
                ds.setDriverClassName("com.mysql.jdbc.Driver");
                ds.setUsername("root");
                ds.setUrl("jdbc:mysql://localhost:3306/drivesync");
                bind(DataSource.class).toInstance(ds);

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
                Gson gson = gsonBuilder.create();
                bind(Gson.class).toInstance(gson);
            }
        });
        return injector;
    }

    public Injector getProduksjonInjector(){
        return injector;
    }
}