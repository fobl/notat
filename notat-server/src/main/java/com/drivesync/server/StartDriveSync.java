package com.drivesync.server;

import com.drivesync.module.GuiceServlet;
import com.google.inject.Inject;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import liquibase.integration.servlet.LiquibaseServletListener;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;

import java.util.HashMap;
import java.util.Map;


public class StartDriveSync {

    private int port;
    private GuiceServletContextListener guiceServlet;

    @Inject
    public StartDriveSync(GuiceServletContextListener guiceServlet) {
        this.guiceServlet = guiceServlet;
    }

    public static void main(String ... args) throws Exception {
        new StartDriveSync(new GuiceServlet()).start();
    }

    public StartDriveSync start() throws Exception  {
        Server server = new Server(0);
        SocketConnector connector = new SocketConnector();
        server.setConnectors(new Connector[] { connector });

        Context context = new Context(server, "/", Context.SESSIONS);
        context.addFilter(GuiceFilter.class, "/*", 0);
        context.addServlet(DefaultServlet.class, "/");
        context.addEventListener(guiceServlet);

        Map<String, String> initParams = new HashMap<>();
        initParams.put("liquibase.changelog", "com/drivesync/db/db.changelog.xml");
        initParams.put("liquibase.datasource", "java:comp/env/jdbc/notat");
        initParams.put("liquibase.onerror.fail", "true");
        context.setInitParams(initParams);
        context.addEventListener(new LiquibaseServletListener());


        server.start();

        port = server.getConnectors()[0].getLocalPort();
        System.out.println(baseurl());
        return this;
    }

    public String baseurl(){
        return "http://localhost:"+port;
    }
}
