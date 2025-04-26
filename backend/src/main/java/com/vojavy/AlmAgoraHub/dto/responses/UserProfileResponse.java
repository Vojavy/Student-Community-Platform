package com.vojavy.AlmAgoraHub.dto.responses;

import com.vojavy.AlmAgoraHub.model.user.UserDetailsExtended;

public class UserProfileResponse {
    private Long userId;
    private String email;
    private boolean active;
    private String domain;
    private String adminEmail;
    private UserDetailsExtended details;
    private String osCislo;
    private String stprIdno;
    private String userName;
    private String jmeno;
    private String prijmeni;
    private String titulPred;
    private String titulZa;
    private String pohlavi;
    private String fakultaSp;
    private String oborKomb;
    private String nazevSp;
    private String kodSp;
    private String formaSp;
    private String typSp;
    private String rocnik;
    private String stav;
    private String mistoVyuky;
    private String cisloKarty;
    private String rozvrhovyKrouzek;
    private String studijniKruh;
    private String evidovanBankovniUcet;

    public UserProfileResponse() {
    }

    // --- Getters and Setters ---

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
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

    public String getFakultaSp() {
        return fakultaSp;
    }

    public void setFakultaSp(String fakultaSp) {
        this.fakultaSp = fakultaSp;
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

    public UserDetailsExtended getDetails() { return details; }  // <<< геттер
    public void setDetails(UserDetailsExtended details) { this.details = details; }  // <<< сеттер

}
