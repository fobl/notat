package com.drivesync.dto;

public class Gruppe extends JsonDto {
    private int id;
    private String gruppenavn;

    public Gruppe(int id, String gruppenavn) {
        this.id = id;
        this.gruppenavn = gruppenavn;
    }

    public int getId() {
        return id;
    }

    public String getGruppenavn() {
        return gruppenavn;
    }

    @Override
    public String toString(){
        return id+":"+gruppenavn;
    }

    public Gruppe fromJson(String json) {
        return gson().fromJson(json, Gruppe.class);
    }

}
