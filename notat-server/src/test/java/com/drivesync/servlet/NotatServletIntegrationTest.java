package com.drivesync.servlet;

import com.drivesync.db.DbService;
import com.drivesync.dto.Gruppe;
import com.drivesync.dto.Notat;
import com.drivesync.module.GuiceTestServlet;
import com.drivesync.server.StartDriveSync;
import com.google.common.io.CharStreams;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotatServletIntegrationTest {

    @Test
    public void skalLagreNotatVedServlet() throws Exception {
        GuiceTestServlet guiceTestServlet = new GuiceTestServlet();
        String baseurl = new StartDriveSync(guiceTestServlet).start().baseurl();

        DbService dbservice = guiceTestServlet.getTestInjector().getInstance(DbService.class);
        dbservice.lagreGruppe(new Gruppe(1, "gruppe 1"));
        Notat notat = new Notat(1, "Tittel", "Innhold", new LocalDateTime(), 1);
        kjørJsonKall(baseurl, notat.toJson());
    }

    protected String kjørJsonKall(String baseurl, String jsonRequest) throws IOException, ServletException {
        URL url = new URL(baseurl+"/notat");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        OutputStreamWriter output = new OutputStreamWriter(connection.getOutputStream());
        output.write(jsonRequest);
        output.flush();
        return CharStreams.toString(new InputStreamReader(connection.getInputStream()));
    }
}
