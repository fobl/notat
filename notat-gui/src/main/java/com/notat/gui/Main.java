package com.notat.gui;

import com.google.common.collect.ImmutableList;
import com.notat.gui.klient.NotatKlient;
import com.notat.server.StartNotatServer;
import com.notat.server.dto.Notat;
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
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    private NotatKlient klient;
    private final TextField tittel = new TextField();
    private ListView notater = new ListView();
    private final TextArea innhold = new TextArea();
    private List<Notat> notatliste;
    private Timer oppdaterTimer;
    private Notat notat;
    private boolean skitten = false;

    private boolean klikketEnter;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        klient = new NotatKlient(new StartNotatServer());

        primaryStage.setTitle("Notat");

        leggTilNotater();
        velgNotat();
        oppdaterNotat();
        tittelBokstavhåndtering();
        innholdBokstavhåndtering();
        listeBokstavhåndtering();

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
                        notat = new Notat(null, tittel.getText(), "", new LocalDateTime(), 1);
                        lagreNotat(notat);
                        leggTilNotater();
                        notatliste = ImmutableList.<Notat>builder().add(notat).addAll(notatliste).build();
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

    private void listeBokstavhåndtering(){
        notater.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    notat = ((Notat)notater.getSelectionModel().getSelectedItem());
                    innhold.requestFocus();
                } else if (keyEvent.getCode() == KeyCode.UP && notater.getSelectionModel().getSelectedIndex() == 0) {
                    tittel.requestFocus();
                }
            }
        }
        );
    }

    private void innholdBokstavhåndtering(){
        innhold.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                notat.setInnhold(innhold.getText());
                skitten = true;
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

    private void lagreNotat(Notat notat){
        klient.nyttNotat(notat);
    }

    private void oppdaterNotat(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(notat != null && skitten){
                    klient.oppdaterNotat(notat);
                    skitten = false;
                }
            }
        };
        oppdaterTimer = new Timer();
        oppdaterTimer.schedule(task, 10L, 1000L);
    }
}