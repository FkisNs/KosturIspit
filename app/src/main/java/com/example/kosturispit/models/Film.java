package com.example.kosturispit.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "film")
public class Film {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "title")
    private String naziv;
    @DatabaseField(columnName = "genre")
    private String genre;
    @DatabaseField(columnName = "godina")
    private String godina;
    @DatabaseField(foreign = true,foreignAutoCreate = true,foreignAutoRefresh = true)
    private Glumac glumac;

    public Film() {
    }

    public Film(int id, String naziv, String genre, String godina, Glumac glumac) {
        this.id = id;
        this.naziv = naziv;
        this.genre = genre;
        this.godina = godina;
        this.glumac = glumac;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getGodina() {
        return godina;
    }

    public void setGodina(String godina) {
        this.godina = godina;
    }

    public Glumac getGlumac() {
        return glumac;
    }

    public void setGlumac(Glumac glumac) {
        this.glumac = glumac;
    }

    @Override
    public String toString()  {
        return naziv + " ("+godina+")\n" + genre;
    }
}

