package com.example.kosturispit.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Glumac.TABLE_NAME_GLUMAC)
public class Glumac {
    public static final String TABLE_NAME_GLUMAC = "glumci";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "ime")
    private String ime;
    @DatabaseField(columnName = "prezime")
    private String prezime;
    @DatabaseField(columnName = "biografija")
    private String biografija;
    @DatabaseField(columnName = "ocena")
    private String ocena;
    @DatabaseField(columnName = "datumR")
    private String dateBirth;
    @DatabaseField(columnName = "image_id")
    private String imageId;
    @ForeignCollectionField(foreignFieldName = "glumac", eager = true)
    private ForeignCollection<Film> filmovi;

    public Glumac() {
    }

    public Glumac(int id, String ime, String prezime, String biografija, String ocena, String dateBirth,String imageId, ForeignCollection<Film> filmovi) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.biografija = biografija;
        this.ocena = ocena;
        this.dateBirth = dateBirth;
        this.imageId = imageId;
        this.filmovi = filmovi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getBiografija() {
        return biografija;
    }

    public void setBiografija(String biografija) {
        this.biografija = biografija;
    }

    public String getOcena() {
        return ocena;
    }

    public void setOcena(String ocena) {
        this.ocena = ocena;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public String getImageId(){
        return imageId;
    }

    public void setImageId(String imageId){
        this.imageId = imageId;
    }

    public ForeignCollection<Film> getFilmovi() {
        return filmovi;
    }

    public void setFilmovi(ForeignCollection<Film> filmovi) {
        this.filmovi = filmovi;
    }

    @Override
    public String toString() {
        return "Glumac{" +
                "ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                '}';
    }
}