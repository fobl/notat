package com.drivesync.dto;

import org.joda.time.LocalDateTime;

public final class Notat extends JsonDto {

    private Integer id;
    private String tittel;
    private String innhold;
    private LocalDateTime endretTid;
    private Integer gruppeid;

    public Notat(Integer id, String tittel, String innhold, LocalDateTime endretTid, Integer gruppeid) {
        this.id = id;
        this.endretTid = new LocalDateTime(endretTid);
        this.tittel = tittel;
        this.innhold = innhold;
        this.gruppeid = gruppeid;
    }

    public Integer getId() {
        return id;
    }

    public String getTittel() {
        return tittel;
    }

    public String getInnhold() {
        return innhold;
    }

    public void setInnhold(String innhold) {
        this.innhold = innhold;
    }

    public LocalDateTime getEndretTid() {
        return endretTid;
    }

    public Integer getGruppeid() {
        return gruppeid;
    }

    public void setGruppeid(Integer gruppeid) {
        this.gruppeid = gruppeid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notat notat = (Notat) o;

        if (id != null ? !id.equals(notat.id) : notat.id != null) return false;

        return true;
    }

    @Override
    public String toString(){
        return id+":"+tittel;
    }

    public static Notat fromJson(String json) {
        return gson().fromJson(json, Notat.class);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
