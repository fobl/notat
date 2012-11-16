package com.drivesync.db;

import com.drivesync.dto.Gruppe;
import com.drivesync.dto.Notat;
import com.google.common.collect.ImmutableList;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.Before;
import org.junit.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.fest.assertions.Assertions.assertThat;

public class DbServiceTest {

    private DbService service;
    private QueryRunner queryRunner;
    private PodamFactory podamFactory;

    @Before
    public void setUp() throws Exception {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem;INIT=RUNSCRIPT FROM 'src/test/resources/create.sql'");
        ds.setUsername("sa");
        service = new DbService(ds);
        queryRunner = new QueryRunner(ds);
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    public void skalHenteVersjon() throws Exception {
        queryRunner.update("insert into versjon (versjon_nr) values('SNAPSHOT-1.0')");
        assertThat(service.hentVersjon()).isEqualTo("SNAPSHOT-1.0");
    }

    @Test
    public void skalHenteNotat() throws Exception {
        Notat notat = podamFactory.manufacturePojo(Notat.class);
        notat.setGruppeid(hentGruppeid());
        service.lagreNotat(notat);

        ImmutableList<Notat> notater = service.hentNotater();

        Notat lagretNotat = notater.get(0);
        assertThat(lagretNotat.getTittel()).isEqualTo(notat.getTittel());
        assertThat(lagretNotat.getInnhold()).isEqualTo(notat.getInnhold());
        assertThat(lagretNotat.getEndretTid()).isEqualTo(notat.getEndretTid());
        assertThat(lagretNotat.getGruppeid()).isEqualTo(notat.getGruppeid());
        assertThat(lagretNotat.getId()).isGreaterThan(0);
    }

    public Integer hentGruppeid(){
        Gruppe gruppe = podamFactory.manufacturePojo(Gruppe.class);
        service.lagreGruppe(gruppe);
        return service.hentGrupper().get(0).getId();
    }
}
