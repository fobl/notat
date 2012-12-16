package com.notat.server.db;

import com.notat.server.dto.Gruppe;
import com.notat.server.dto.Notat;
import com.google.common.collect.ImmutableList;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;

public class DbService {

    private Logger logg = LoggerFactory.getLogger(DbService.class);
    private QueryRunner queryRunner;

    public DbService() {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds  = (DataSource)ctx.lookup("jdbc/notat");
            queryRunner = new QueryRunner(ds);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public ImmutableList<Notat> hentNotater(){
        try {
            ImmutableList<Notat> respons = queryRunner.query("select * from Notat", new NotatHandler());
            logg.info("Hentet "+respons.size()+" notater");
            return respons;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void slettNotat(int id){
        try {
            queryRunner.update("delete from Notat where id = ?", id);
            logg.info("Slettet notat "+id);
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void lagreGruppe(Gruppe gruppe){
        try {
            queryRunner.update("insert into Gruppe (gruppenavn) values(?)", gruppe.getGruppenavn());
            logg.info("Lagret gruppe "+gruppe);
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public ImmutableList<Gruppe> hentGrupper(){
        try {
            ImmutableList<Gruppe> respons = queryRunner.query("select * from Gruppe", new GruppeHandler());
            logg.info("Hentet grupper "+respons.size());
            return respons;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void lagreNotat(Notat notat){
        try {
            queryRunner.update("insert into Notat (tittel, innhold, endret_tid, gruppe_id) values(?,?,?,?)",
                    notat.getTittel(), notat.getInnhold(), notat.getEndretTid().toDate(), notat.getGruppeid());
            logg.info("Lagret notat "+notat);
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void oppdaterNotat(Notat notat) {
        try {
            queryRunner.update("update Notat set tittel = ?,  innhold = ?, endret_tid = ?, gruppe_id = ? where id = ?",
                    notat.getTittel(), notat.getInnhold(), notat.getEndretTid().toDate(), notat.getGruppeid(), notat.getId());
            logg.info("Oppdaterte notat "+notat);
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

//    }

    public void slettGruppe(int id) {
        try {
            queryRunner.update("delete from gruppe where id = ?", id);
            logg.info("Slettet gruppe "+id);
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Gruppe oppdaterGruppenavn(Gruppe gruppe) {
        try {
            queryRunner.update("update Gruppe set gruppenavn = ? where id = ?",
                    gruppe.getGruppenavn(), gruppe.getId());
            logg.info("Oppdaterte gruppenavn til "+gruppe.getGruppenavn()+" for "+gruppe.getId());
            return queryRunner.query("select * from Gruppe where id = ?", new GruppeHandler(), gruppe.getId()).get(0);
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
