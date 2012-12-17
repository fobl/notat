package com.notat.gui.klient;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import com.google.gson.internal.StringMap;
import com.notat.server.StartNotatServer;
import com.notat.server.dto.Notat;
import com.notat.server.tools.GsonFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NotatKlient {

    private String baseurl;
    private Logger logg = LoggerFactory.getLogger("NotatKlient.class");


    public NotatKlient(StartNotatServer server) throws Exception {
        baseurl = server.start().baseurl();
    }

    public void nyttNotat(Notat notat) {
        try {
            logg.info("Legger til notat "+notat);
            HttpPost post = new HttpPost(baseurl + "/notat/");
            StringEntity json = new StringEntity(notat.toJson(), ContentType.APPLICATION_JSON);
            post.setEntity(json);
            HttpResponse respons = new DefaultHttpClient().execute(post);
            if(respons.getStatusLine().getStatusCode() != 201){
                throw new RuntimeException("Feil i statuskode fra nytt notat "+baseurl+"/notat/, statuskode: "+respons.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void oppdaterNotat(Notat notat) {
        try {
            logg.info("Oppdaterer notat "+notat);
            HttpPut put = new HttpPut(baseurl + "/notat/");
            StringEntity json = new StringEntity(notat.toJson(), ContentType.APPLICATION_JSON);
            put.setEntity(json);
            HttpResponse respons = new DefaultHttpClient().execute(put);
            if(respons.getStatusLine().getStatusCode() != 200){
                throw new RuntimeException("Feil i statuskode fra oppdaterer notat "+baseurl+"/notat/, statuskode: "+respons.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void slettNotat(int id) {
        try {
            logg.info("Sletter notat "+id);
            HttpDelete delete = new HttpDelete(baseurl + "/notat/" + id);
            HttpResponse respons = new DefaultHttpClient().execute(delete);
            if(respons.getStatusLine().getStatusCode() != 200){
                throw new RuntimeException("Feil i statuskode fra sletter notat "+baseurl+"/notat/"+id+", statuskode: "+respons.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ImmutableList<Notat> hentNotater() {
        try {
            logg.info("Henter notater");
            HttpGet get = new HttpGet(baseurl + "/notat/");
            HttpResponse response = new DefaultHttpClient().execute(get);
            if(response.getStatusLine().getStatusCode() != 200){
                throw new RuntimeException("Feil i statuskode fra henter notat "+baseurl+"/notat/, statuskode: "+response.getStatusLine().getStatusCode());
            }
            return hentUtNotater(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private ImmutableList<Notat> hentUtNotater(HttpResponse response) throws IOException {
        try (InputStream inputstream = response.getEntity().getContent()) {
            String json = CharStreams.toString(new InputStreamReader(inputstream));
            List<StringMap> list = GsonFactory.gson().fromJson(json, List.class);
            List<Notat> notater = new ArrayList<>();
            notaterFraJson(list, notater);
            logg.info("Hentet ut "+list.size()+" notater");
            return ImmutableList.copyOf(notater);
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
