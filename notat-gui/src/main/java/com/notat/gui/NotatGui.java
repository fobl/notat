package com.notat.gui;

import com.drivesync.dto.Notat;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.notat.klient.NotatKlient;
import com.notat.modul.GuiceModul;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class NotatGui extends Application {

    private NotatKlient klient;
    private final TextField tittel = new TextField();
    private ListView notater = new ListView();
    private final TextArea innhold = new TextArea();
    private List<Notat> notatliste;

    private boolean klikketEnter;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Injector injector = Guice.createInjector(new GuiceModul());
        klient = injector.getInstance(NotatKlient.class);

        primaryStage.setTitle("Notat");

        leggTilNotater();
        velgNotat();
        tittelBokstavhåndtering();
        notaterBokstavhåndtering();
        innholdBokstavhåndtering();

        BorderPane bp = new BorderPane();
        bp.setTop(tittel);
        bp.setCenter(notater);
        bp.setBottom(innhold);

        Scene scene = new Scene(bp, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void tittelBokstavhåndtering() {
        tittel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    if (klikketEnter) {
                        klikketEnter = false;
                        notatliste = ImmutableList.<Notat>builder().add(new Notat(null, tittel.getText(), "", new LocalDateTime(), 1)).addAll(notatliste).build();
                        innhold.requestFocus();
                    } else {
                        klikketEnter = true;
                    }
                } else if(keyEvent.getCode() == KeyCode.DOWN){
                    notater.requestFocus();
                } else {
                    notater.setItems(FXCollections.observableList(søk()));
                }
            }
        }
        );
    }

    private void notaterBokstavhåndtering(){
        notater.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                ((Notat)notater.getSelectionModel().getSelectedItem()).setInnhold(innhold.getText());
            }
        }
        );
    }

    private void innholdBokstavhåndtering(){
        notater.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    innhold.requestFocus();
                } else if (keyEvent.getCode() == KeyCode.UP && notater.getSelectionModel().getSelectedIndex() == 0) {
                    tittel.requestFocus();
                }
            }
        }
        );
    }

    private ImmutableList<Notat> søk() {
        List<Notat> tmpNotatliste = new ArrayList();
        for(Notat notat : notatliste){
            if(notat.getTittel().toLowerCase().contains(tittel.getText().toLowerCase()) || notat.getInnhold().toLowerCase().contains(tittel.getText().toLowerCase())){
                tmpNotatliste.add(notat);
            }
        }
        return ImmutableList.copyOf(tmpNotatliste);
    }

    private void velgNotat() {
        notater.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Notat>() {
                    @Override
                    public void changed(ObservableValue<? extends Notat> observableValue, Notat notat, Notat notat1) {
                        if (notat != null) {
                            tittel.setText(notat.getTittel());
                            innhold.setText(notat.getInnhold());
                        }
                    }
                });
    }



    private void leggTilNotater() {
        notatliste = klient.hentNotater();
        notater.setItems(FXCollections.observableList(notatliste));
    }
}