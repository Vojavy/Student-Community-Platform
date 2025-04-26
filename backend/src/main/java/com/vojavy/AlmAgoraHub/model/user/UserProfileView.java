package com.vojavy.AlmAgoraHub.model.user;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@Entity
@Table(name = "user_profile_view")
@Immutable
public class UserProfileView implements Serializable {
    @Id
    @Column(name = "user_id")
    private Long userId;

    private String email;
    private Boolean active;
    private String domain;
    private String adminEmail;

    @Convert(converter = UserDetailsExtended.ConverterImpl.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
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

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getActive() {
        return active;
    }

    public String getDomain() {
        return domain;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getOsCislo() {
        return osCislo;
    }

    public String getStprIdno() {
        return stprIdno;
    }

    public String getUserName() {
        return userName;
    }

    public String getJmeno() {
        return jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public String getTitulPred() {
        return titulPred;
    }

    public String getTitulZa() {
        return titulZa;
    }

    public String getPohlavi() {
        return pohlavi;
    }

    public String getFakultaSp() {
        return fakultaSp;
    }

    public String getOborKomb() {
        return oborKomb;
    }

    public String getNazevSp() {
        return nazevSp;
    }

    public String getKodSp() {
        return kodSp;
    }

    public String getFormaSp() {
        return formaSp;
    }

    public String getTypSp() {
        return typSp;
    }

    public String getRocnik() {
        return rocnik;
    }

    public String getStav() {
        return stav;
    }

    public String getMistoVyuky() {
        return mistoVyuky;
    }

    public String getCisloKarty() {
        return cisloKarty;
    }

    public String getRozvrhovyKrouzek() {
        return rozvrhovyKrouzek;
    }

    public String getStudijniKruh() {
        return studijniKruh;
    }

    public String getEvidovanBankovniUcet() {
        return evidovanBankovniUcet;
    }

    public UserDetailsExtended getDetails() { return details; }
}
