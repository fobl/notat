package com.notat.gui.klient;

import com.google.common.collect.ImmutableList;
import com.notat.server.StartNotatServer;
import com.notat.server.dto.Notat;
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

    @Before
    public void setUp() throws Exception {
        System.setProperty(StartNotatServer.JDBC_URL, "jdbc:h2:mem:test_mem;");
        System.setProperty(StartNotatServer.JDBC_USERNAME, "sa");
        System.setProperty(StartNotatServer.JDBC_PASSWORD, "");
        System.setProperty(StartNotatServer.JDBC_DRIVER, "org.h2.Driver");
        klient = new NotatKlient(new StartNotatServer());
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
