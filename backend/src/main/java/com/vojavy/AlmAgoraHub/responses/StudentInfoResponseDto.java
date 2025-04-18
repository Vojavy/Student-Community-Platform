package com.vojavy.AlmAgoraHub.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentInfoResponseDto {

    @JsonProperty("osCislo")
    private String osCislo;

    @JsonProperty("stprIdno")
    private String stprIdno;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("jmeno")
    private String jmeno;

    @JsonProperty("prijmeni")
    private String prijmeni;

    @JsonProperty("titulPred")
    private String titulPred;

    @JsonProperty("titulZa")
    private String titulZa;

    @JsonProperty("pohlavi")
    private String pohlavi;

    @JsonProperty("fakultaSp")
    private String fakultaSp;

    @JsonProperty("oborKomb")
    private String oborKomb;

    @JsonProperty("nazevSp")
    private String nazevSp;

    @JsonProperty("kodSp")
    private String kodSp;

    @JsonProperty("formaSp")
    private String formaSp;

    @JsonProperty("typSp")
    private String typSp;

    @JsonProperty("rocnik")
    private String rocnik;

    @JsonProperty("stav")
    private String stav;

    @JsonProperty("mistoVyuky")
    private String mistoVyuky;

    @JsonProperty("cisloKarty")
    private String cisloKarty;

    @JsonProperty("rozvrhovyKrouzek")
    private String rozvrhovyKrouzek;

    @JsonProperty("studijniKruh")
    private String studijniKruh;

    @JsonProperty("evidovanBankovniUcet")
    private String evidovanBankovniUcet;

    // Getters and setters

    public String getOsCislo() { return osCislo; }
    public void setOsCislo(String osCislo) { this.osCislo = osCislo; }

    public String getStprIdno() { return stprIdno; }
    public void setStprIdno(String stprIdno) { this.stprIdno = stprIdno; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getJmeno() { return jmeno; }
    public void setJmeno(String jmeno) { this.jmeno = jmeno; }

    public String getPrijmeni() { return prijmeni; }
    public void setPrijmeni(String prijmeni) { this.prijmeni = prijmeni; }

    public String getTitulPred() { return titulPred; }
    public void setTitulPred(String titulPred) { this.titulPred = titulPred; }

    public String getTitulZa() { return titulZa; }
    public void setTitulZa(String titulZa) { this.titulZa = titulZa; }

    public String getPohlavi() { return pohlavi; }
    public void setPohlavi(String pohlavi) { this.pohlavi = pohlavi; }

    public String getFakultaSp() { return fakultaSp; }
    public void setFakultaSp(String fakultaSp) { this.fakultaSp = fakultaSp; }

    public String getOborKomb() { return oborKomb; }
    public void setOborKomb(String oborKomb) { this.oborKomb = oborKomb; }

    public String getNazevSp() { return nazevSp; }
    public void setNazevSp(String nazevSp) { this.nazevSp = nazevSp; }

    public String getKodSp() { return kodSp; }
    public void setKodSp(String kodSp) { this.kodSp = kodSp; }

    public String getFormaSp() { return formaSp; }
    public void setFormaSp(String formaSp) { this.formaSp = formaSp; }

    public String getTypSp() { return typSp; }
    public void setTypSp(String typSp) { this.typSp = typSp; }

    public String getRocnik() { return rocnik; }
    public void setRocnik(String rocnik) { this.rocnik = rocnik; }

    public String getStav() { return stav; }
    public void setStav(String stav) { this.stav = stav; }

    public String getMistoVyuky() { return mistoVyuky; }
    public void setMistoVyuky(String mistoVyuky) { this.mistoVyuky = mistoVyuky; }

    public String getCisloKarty() { return cisloKarty; }
    public void setCisloKarty(String cisloKarty) { this.cisloKarty = cisloKarty; }

    public String getRozvrhovyKrouzek() { return rozvrhovyKrouzek; }
    public void setRozvrhovyKrouzek(String rozvrhovyKrouzek) { this.rozvrhovyKrouzek = rozvrhovyKrouzek; }

    public String getStudijniKruh() { return studijniKruh; }
    public void setStudijniKruh(String studijniKruh) { this.studijniKruh = studijniKruh; }

    public String getEvidovanBankovniUcet() { return evidovanBankovniUcet; }
    public void setEvidovanBankovniUcet(String evidovanBankovniUcet) { this.evidovanBankovniUcet = evidovanBankovniUcet; }

}
