package com.drivesync.server;

import com.drivesync.module.GuiceServlet;
import com.google.inject.Inject;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import liquibase.integration.servlet.LiquibaseServletListener;
import org.apache.commons.dbcp.BasicDataSource;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.plus.naming.EnvEntry;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;

import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;


public class StartDriveSync {

    public static final String JDBC_URL = "jdbc_url";
    public static final String JDBC_USERNAME = "jdbc_username";
    public static final String JDBC_PASSWORD = "jdbc_password";
    public static final String JDBC_DRIVER = "jdbc_driver";
    private int port;
    private GuiceServletContextListener guiceServlet;

    @Inject
    public StartDriveSync(GuiceServletContextListener guiceServlet) {
        this.guiceServlet = guiceServlet;
    }

    public static void main(String... args) throws Exception {
        new StartDriveSync(new GuiceServlet()).start();
    }

    public StartDriveSync start() throws Exception {
        Server server = new Server(0);
        SocketConnector connector = new SocketConnector();
        server.setConnectors(new Connector[]{connector});

        Context context = guice(server);
        datasource();
        liquibase(context);

        server.start();
        port = server.getConnectors()[0].getLocalPort();
        System.out.println(baseurl());
        return this;
    }

    private Context guice(Server server) {
        Context context = new Context(server, "/", Context.SESSIONS);
        context.addFilter(GuiceFilter.class, "/*", 0);
        context.addServlet(DefaultServlet.class, "/");
        context.addEventListener(guiceServlet);
        return context;
    }

    private void datasource() throws NamingException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(System.getProperty(JDBC_URL, "jdbc:mysql://localhost:3306/notat"));
        dataSource.setUsername(System.getProperty(JDBC_USERNAME, "root"));
        dataSource.setPassword(System.getProperty(JDBC_PASSWORD, ""));
        dataSource.setValidationQuery("select 1");
        dataSource.setDriverClassName(System.getProperty(JDBC_DRIVER, "com.mysql.jdbc.Driver"));
        new EnvEntry("jdbc/notat", dataSource);
    }

    private void liquibase(Context context) {
        Map<String, String> initParams = new HashMap<>();
        initParams.put("liquibase.changelog", "com/drivesync/db/db.changelog.xml");
        initParams.put("liquibase.datasource", "jdbc/notat");
        initParams.put("liquibase.onerror.fail", "true");
        context.setInitParams(initParams);
        context.addEventListener(new LiquibaseServletListener());
    }

    public String baseurl() {
        return "http://localhost:" + port;
    }
}
