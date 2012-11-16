package com.notat.klient;

import com.drivesync.dto.Notat;
import com.drivesync.module.GuiceTestServlet;
import com.drivesync.server.StartDriveSync;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.notat.modul.GuiceModul;
import org.junit.Before;
import org.junit.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class NotatKlientIntegrasjonTest {

    private NotatKlient klient;
    private PodamFactory podamFactory = new PodamFactoryImpl();

    @Before
    public void setUp() throws Exception {
        GuiceTestServlet testServlet = new GuiceTestServlet();
        testServlet.getTestInjector().getBinding(StartDriveSync.class);

        Injector injector = Guice.createInjector(new GuiceModul());
        klient = injector.getInstance(NotatKlient.class);
    }

    @Test
    public void skalHenteNotater() throws Exception {
        Notat notat = podamFactory.manufacturePojo(Notat.class);
        klient.nyttNotat(notat);

        ImmutableList<Notat> notater = klient.hentNotater();
        System.out.println(notater);
    }
}
