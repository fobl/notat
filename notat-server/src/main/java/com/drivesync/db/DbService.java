package com.drivesync.db;

import com.drivesync.dto.Gruppe;
import com.drivesync.dto.Notat;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

public class DbService {

    private QueryRunner queryRunner;

    @Inject
    public DbService(DataSource ds) {
        queryRunner = new QueryRunner(ds);
    }

    public String hentVersjon() {
        try {
            Map<String,Object> result = queryRunner.query("select * from versjon", new MapHandler());
            return result == null ? null : result.get("versjon_nr").toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ImmutableList<Notat> hentNotater(){
        try {
            return queryRunner.query("select * from Notat", new NotatHandler());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void lagreGruppe(Gruppe gruppe){
        try {
            queryRunner.update("insert into Gruppe (gruppenavn) values(?)", gruppe.getGruppenavn());
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public ImmutableList<Gruppe> hentGrupper(){
        try {
            return queryRunner.query("select * from Gruppe", new GruppeHandler());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void lagreNotat(Notat notat){
        try {
            queryRunner.update("insert into Notat (tittel, innhold, endret_tid, gruppe_id) values(?,?,?,?)",
                    notat.getTittel(), notat.getInnhold(), notat.getEndretTid().toDate(), notat.getGruppeid());
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

//    public void oppdaterNotat(){
//
//    }

}
