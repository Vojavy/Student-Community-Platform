package com.vojavy.AlmAgoraHub.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_is_data")
public class UserISData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String osCislo;
    private String stprIdno;
    private String userName;
    private String email;

    private String jmeno;
    private String prijmeni;
    private String titulPred;
    private String titulZa;
    private String pohlavi;

    private String oborKomb;
    private String nazevSp;
    private String kodSp;
    private String fakultaSp;
    private String formaSp;
    private String typSp;
    private String rocnik;
    private String stav;
    private String mistoVyuky;

    private String cisloKarty;
    private String rozvrhovyKrouzek;
    private String studijniKruh;
    private String evidovanBankovniUcet;

    private Instant createdAt;
    private Instant updatedAt;

    public UserISData() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Все геттеры и сеттеры ниже (можно сгенерировать в IDE)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOsCislo() {
        return osCislo;
    }

    public void setOsCislo(String osCislo) {
        this.osCislo = osCislo;
    }

    public String getStprIdno() {
        return stprIdno;
    }

    public void setStprIdno(String stprIdno) {
        this.stprIdno = stprIdno;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public String getTitulPred() {
        return titulPred;
    }

    public void setTitulPred(String titulPred) {
        this.titulPred = titulPred;
    }

    public String getTitulZa() {
        return titulZa;
    }

    public void setTitulZa(String titulZa) {
        this.titulZa = titulZa;
    }

    public String getPohlavi() {
        return pohlavi;
    }

    public void setPohlavi(String pohlavi) {
        this.pohlavi = pohlavi;
    }

    public String getOborKomb() {
        return oborKomb;
    }

    public void setOborKomb(String oborKomb) {
        this.oborKomb = oborKomb;
    }

    public String getNazevSp() {
        return nazevSp;
    }

    public void setNazevSp(String nazevSp) {
        this.nazevSp = nazevSp;
    }

    public String getKodSp() {
        return kodSp;
    }

    public void setKodSp(String kodSp) {
        this.kodSp = kodSp;
    }

    public String getFakultaSp() {
        return fakultaSp;
    }

    public void setFakultaSp(String fakultaSp) {
        this.fakultaSp = fakultaSp;
    }

    public String getFormaSp() {
        return formaSp;
    }

    public void setFormaSp(String formaSp) {
        this.formaSp = formaSp;
    }

    public String getTypSp() {
        return typSp;
    }

    public void setTypSp(String typSp) {
        this.typSp = typSp;
    }

    public String getRocnik() {
        return rocnik;
    }

    public void setRocnik(String rocnik) {
        this.rocnik = rocnik;
    }

    public String getStav() {
        return stav;
    }

    public void setStav(String stav) {
        this.stav = stav;
    }

    public String getMistoVyuky() {
        return mistoVyuky;
    }

    public void setMistoVyuky(String mistoVyuky) {
        this.mistoVyuky = mistoVyuky;
    }

    public String getCisloKarty() {
        return cisloKarty;
    }

    public void setCisloKarty(String cisloKarty) {
        this.cisloKarty = cisloKarty;
    }

    public String getRozvrhovyKrouzek() {
        return rozvrhovyKrouzek;
    }

    public void setRozvrhovyKrouzek(String rozvrhovyKrouzek) {
        this.rozvrhovyKrouzek = rozvrhovyKrouzek;
    }

    public String getStudijniKruh() {
        return studijniKruh;
    }

    public void setStudijniKruh(String studijniKruh) {
        this.studijniKruh = studijniKruh;
    }

    public String getEvidovanBankovniUcet() {
        return evidovanBankovniUcet;
    }

    public void setEvidovanBankovniUcet(String evidovanBankovniUcet) {
        this.evidovanBankovniUcet = evidovanBankovniUcet;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
