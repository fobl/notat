package com.notat.server.servlet;

import com.google.common.collect.ImmutableList;
import com.notat.server.db.DbService;
import com.notat.server.dto.Notat;
import com.notat.server.tools.GsonFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class NotatServlet extends HttpServlet {

    Logger log = Logger.getLogger("NotatServlet.class");
    private DbService dbService;

    public NotatServlet() {
        this.dbService = new DbService();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.info("notat 'doGet()'");
            ImmutableList<Notat> notater = dbService.hentNotater();
            resp.setStatus(200);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(GsonFactory.gson().toJson(notater));
        } catch (IOException e) {
            resp.setStatus(500);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.info("Nytt notat 'doPost()'");
            Notat notat = GsonFactory.gson().fromJson(req.getReader(), Notat.class);
            log.info("NOTAT " + notat);
            dbService.lagreNotat(notat);
            resp.setStatus(201);
        } catch (IOException e) {
            resp.setStatus(500);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.info("Oppdater notat 'doPut()'");
            Notat notat = GsonFactory.gson().fromJson(req.getReader(), Notat.class);
            dbService.oppdaterNotat(notat);
            resp.setStatus(200);
        } catch (IOException e) {
            resp.setStatus(500);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String[] url = req.getRequestURI().split("/");
            String id = url[url.length-1];
            log.info("Slett notat 'doDelete()', id="+id);
            dbService.slettNotat(Integer.valueOf(id));
        } catch(Exception e){
            resp.setStatus(500);
            throw new RuntimeException(e);
        }
    }
}
