package com.notat.server;

import liquibase.integration.servlet.LiquibaseServletListener;
import org.apache.commons.dbcp.BasicDataSource;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.plus.naming.EnvEntry;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;


public class StartNotatServer {

    private Logger logg = LoggerFactory.getLogger(StartNotatServer.class);
    public static final String JDBC_URL = "jdbc_url";
    public static final String JDBC_USERNAME = "jdbc_username";
    public static final String JDBC_PASSWORD = "jdbc_password";
    public static final String JDBC_DRIVER = "jdbc_driver";
    private int port;

    public StartNotatServer() {
    }

    public static void main(String... args) throws Exception {
        StartNotatServer start = new StartNotatServer();
        start.start();
    }

    public StartNotatServer start() throws Exception {
        Server server = new Server(0);
        SocketConnector connector = new SocketConnector();
        server.setConnectors(new Connector[]{connector});

        Context context = lagContext();
        server.addHandler(context);

        datasource();
        liquibase(context);

        server.start();
        port = server.getConnectors()[0].getLocalPort();
        System.out.println(baseurl());
        return this;
    }

    private Context lagContext() {
        String path = path() +"notat-server/src/main/webapp";
        logg.info("Setter opp webapps på "+path);
        WebAppContext context = new WebAppContext(path, "/");
        return context;
    }

    public static String path(){
        String path = System.getProperty("user.dir");
        //hack for å få path til notat prosjektet både i gradle og idea.
        String[] split = path.split("notat-");
        return split[0];
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
        initParams.put("liquibase.changelog", "com/notat/server/db/db.changelog.xml");
        initParams.put("liquibase.datasource", "jdbc/notat");
        initParams.put("liquibase.onerror.fail", "true");
        context.setInitParams(initParams);
        context.addEventListener(new LiquibaseServletListener());
    }

    public String baseurl() {
        return "http://localhost:" + port;
    }
}
