package com.notat.klient;

import com.drivesync.dto.Notat;
import com.drivesync.server.StartDriveSync;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import com.google.inject.Inject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.LocalDateTime;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NotatKlient {

    private String baseurl;
    private Gson gson;

    @Inject
    public NotatKlient(StartDriveSync server, Gson gson) throws Exception {
        baseurl = server.start().baseurl();
        this.gson = gson;
    }

    public void nyttNotat(Notat notat) {
        try {
            HttpPost post = new HttpPost(baseurl + "/notat/");
            StringEntity json = new StringEntity(notat.toJson(), ContentType.APPLICATION_JSON);
            post.setEntity(json);
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.execute(post);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ImmutableList<Notat> hentNotater() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(baseurl + "/notat/").openConnection();
            String json = CharStreams.toString(new InputStreamReader(connection.getInputStream()));
            List<StringMap> list =  gson.fromJson(json, List.class);
            List<Notat> notater = new ArrayList<>();
                notaterFraJson(list, notater);
            return ImmutableList.copyOf(notater);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void notaterFraJson(List<StringMap> list, List<Notat> notater) {
        for (StringMap item : list) {
            String[] tid = (item.get("endretTid") + "").split(":");
            LocalDateTime endretTid = new LocalDateTime(tid(tid[0]), tid(tid[1]), tid(tid[2]), tid(tid[3]), tid(tid[4]), tid(tid[5]));

            notater.add(
                    new Notat(
                            ((Double) item.get("id")).intValue(),
                            (String) item.get("tittel"),
                            (String) item.get("innhold"),
                            endretTid,
                            ((Double) item.get("gruppeid")).intValue()));
        }
    }

    private Integer tid(String tid) {
        return Integer.valueOf(tid);
    }
}
