package com.drivesync.db;

import com.drivesync.dto.Gruppe;
import com.drivesync.dto.Notat;
import com.google.common.collect.ImmutableList;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.plus.naming.EnvEntry;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.fest.assertions.Assertions.assertThat;

public class DbServiceTest {

    private DbService service;
    private PodamFactory podamFactory;
    private String path = System.getProperty("user.dir");
    BasicDataSource ds = new BasicDataSource();

    @Before
    public void setUp() throws Exception {
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:test_mem;");
        ds.setUsername("sa");
        String fil = "/src/main/resources/com/drivesync/db/db.changelog.xml";
        Liquibase liquibase = new Liquibase(fil, new FileSystemResourceAccessor(path), new JdbcConnection(ds.getConnection()));
        liquibase.update("");
        new EnvEntry("jdbc/notat", ds);
        service = new DbService();
        podamFactory = new PodamFactoryImpl();
    }

    @After
    public void tearDown() throws Exception {
        String fil ="/src/test/resources/com/drivesync/db/db.clean.xml";
        Liquibase liquibase = new Liquibase(fil, new FileSystemResourceAccessor(path), new JdbcConnection(ds.getConnection()));
        liquibase.update("");
    }

    @Test
    public void skalHenteNotat() throws Exception {
        Notat notat = lagreNyttNotat();

        ImmutableList<Notat> notater = service.hentNotater();

        Notat lagretNotat = notater.get(0);
        assertThat(lagretNotat.getTittel()).isEqualTo(notat.getTittel());
        assertThat(lagretNotat.getInnhold()).isEqualTo(notat.getInnhold());
        assertThat(lagretNotat.getEndretTid()).isEqualTo(notat.getEndretTid());
        assertThat(lagretNotat.getGruppeid()).isEqualTo(notat.getGruppeid());
        assertThat(lagretNotat.getId()).isGreaterThan(0);
    }

    @Test
    public void skalOppdatereNotat() throws Exception {
        lagreNyttNotat();
        Notat notat = service.hentNotater().get(0);
        String innhold = "nytt innhold";
        notat.setInnhold(innhold);
        Notat oppdatertNotat = service.oppdaterNotat(notat);
        assertThat(oppdatertNotat.getInnhold()).isEqualTo(innhold);
    }

    @Test
    public void skalSletteNotat() throws Exception {
        lagreNyttNotat();
        Notat notat = service.hentNotater().get(0);
        service.slettNotat(notat.getId());
        assertThat(service.hentNotater()).isEmpty();
    }

    @Test
    public void skalSletteGruppe() throws Exception {
        lagreNyGruppe();
        Gruppe gruppe = service.hentGrupper().get(0);
        service.slettGruppe(gruppe.getId());
        assertThat(service.hentGrupper()).isEmpty();
    }

    @Test
    public void skalOppdaterGruppe() throws Exception {
        lagreNyGruppe();
        Gruppe gruppe = service.hentGrupper().get(0);
        String gruppenavn = "nytt gruppenavn";
        gruppe.setGruppenavn(gruppenavn);
        Gruppe oppdatertGruppe = service.oppdaterGruppenavn(gruppe);
    }

    private Notat lagreNyttNotat() {
        Notat notat = podamFactory.manufacturePojo(Notat.class);
        notat.setGruppeid(lagreNyGruppe());
        service.lagreNotat(notat);
        return notat;
    }

    public Integer lagreNyGruppe(){
        Gruppe gruppe = podamFactory.manufacturePojo(Gruppe.class);
        service.lagreGruppe(gruppe);
        return service.hentGrupper().get(0).getId();
    }
}
