package com.notat.klient;

import com.drivesync.dto.Notat;
import com.drivesync.server.StartDriveSync;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.notat.modul.GuiceModul;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NotatKlientIntegrasjonTest {

    private NotatKlient klient;
    private PodamFactory podamFactory = new PodamFactoryImpl();
    private String path = System.getProperty("user.dir");

    @Before
    public void setUp() throws Exception {
        System.setProperty(StartDriveSync.JDBC_URL, "jdbc:h2:mem:test_mem;");
        System.setProperty(StartDriveSync.JDBC_USERNAME, "sa");
        System.setProperty(StartDriveSync.JDBC_PASSWORD, "");
        System.setProperty(StartDriveSync.JDBC_DRIVER, "org.h2.Driver");
        Injector injector = Guice.createInjector(new GuiceModul());
        klient = injector.getInstance(NotatKlient.class);
    }

    @After
    public void tearDown() throws Exception {
        DbTestUtil.slettTabellerFraH2Db();
    }

    @Test
    public void skalLageNyttNotat() throws Exception {
        Notat notat = lagNotat();
        ImmutableList<Notat> notater = klient.hentNotater();
        assertEquals(notat.getTittel(), notater.get(0).getTittel());
    }

    @Test
    public void skalOppdaterNotat() throws Exception {
        lagNotat();
        Notat notat = klient.hentNotater().get(0);
        notat.setInnhold("nytt innhold");
        klient.oppdaterNotat(notat);
        Notat oppdatertNotat = klient.hentNotater().get(0);
        assertEquals("nytt innhold", oppdatertNotat.getInnhold());
    }

    @Test
    public void skalSletteNotat() throws Exception {
        lagNotat();
        Notat lagretNotat = klient.hentNotater().get(0);
        klient.slettNotat(lagretNotat.getId());
        assertTrue(klient.hentNotater().isEmpty());
    }

    private Notat lagNotat() {
        Notat notat = podamFactory.manufacturePojo(Notat.class);
        klient.nyttNotat(notat);
        return notat;
    }
}
