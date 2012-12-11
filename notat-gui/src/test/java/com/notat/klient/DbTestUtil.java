package com.notat.klient;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.commons.dbcp.BasicDataSource;

public class DbTestUtil {

    private static String path = System.getProperty("user.dir");

    public static void settOppTabellerPåH2Db() throws Exception {
        String fil = "notat-server/src/main/resources/com/drivesync/db/db.changelog.xml";
        Liquibase liquibase = new Liquibase(fil, new FileSystemResourceAccessor(path), new JdbcConnection(dataSourceForH2Db().getConnection()));
        liquibase.update("");
    }

    public static void slettTabellerFraH2Db() throws Exception {
        String fil ="notat-server/src/test/resources/com/drivesync/db/db.clean.xml";
        Liquibase liquibase = new Liquibase(fil, new FileSystemResourceAccessor(path), new JdbcConnection(dataSourceForH2Db().getConnection()));
        liquibase.update("");
    }

    public static BasicDataSource dataSourceForH2Db(){
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:test_mem;");
        ds.setUsername("sa");
        return ds;
    }
}
