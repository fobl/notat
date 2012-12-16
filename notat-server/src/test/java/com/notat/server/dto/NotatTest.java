package com.notat.server.dto;

import org.junit.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.fest.assertions.Assertions.assertThat;

public class NotatTest {

    private PodamFactory podamFactory = new PodamFactoryImpl();

    @Test
    public void skalKonvertereTilOgFraJson() throws Exception {
        Notat notat = podamFactory.manufacturePojo(Notat.class);
        Notat notatFromJson = Notat.fromJson(notat.toJson());

        assertThat(notat.getId()).isEqualTo(notatFromJson.getId());
        assertThat(notat.getTittel()).isEqualTo(notatFromJson.getTittel());
        assertThat(notat.getInnhold()).isEqualTo(notatFromJson.getInnhold());
        assertThat(notat.getEndretTid()).isEqualTo(notatFromJson.getEndretTid());
    }
}
