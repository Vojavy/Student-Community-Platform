package com.vojavy.AlmAgoraHub.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Extended user profile details stored as JSONB in the database.
 * This includes personal and social metadata, not used for authentication or core identity.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsExtended {

    private String bio;
    private List<String> interests;
    private LocalDate birthDate;
    private List<String> languages;
    private Map<String, String> contacts; // standard contact links
    private Map<String, String> other;    // additional links or contact methods
    private String location;
    private String website;
    private String status;
    private List<String> skills;

    public UserDetailsExtended() {
        this.bio = "";
        this.interests = new ArrayList<>();
        this.languages = new ArrayList<>();
        this.contacts = new HashMap<>();
        this.other = new HashMap<>();
        this.skills = new ArrayList<>();
        this.location = "";
        this.website = "";
        this.status = "";
    }

    // --- Full constructor (optional) ---
    public UserDetailsExtended(String bio,
                               List<String> interests,
                               LocalDate birthDate,
                               List<String> languages,
                               Map<String, String> contacts,
                               Map<String, String> other,
                               String location,
                               String website,
                               String status,
                               List<String> skills) {
        this.bio = bio;
        this.interests = interests;
        this.birthDate = birthDate;
        this.languages = languages;
        this.contacts = contacts;
        this.other = other;
        this.location = location;
        this.website = website;
        this.status = status;
        this.skills = skills;
    }

    // --- Getters and Setters ---
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Map<String, String> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, String> contacts) {
        this.contacts = contacts;
    }

    public Map<String, String> getOther() {
        return other;
    }

    public void setOther(Map<String, String> other) {
        this.other = other;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    // --- Equality & Debug Support ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDetailsExtended)) return false;
        UserDetailsExtended that = (UserDetailsExtended) o;
        return Objects.equals(bio, that.bio) &&
                Objects.equals(interests, that.interests) &&
                Objects.equals(birthDate, that.birthDate) &&
                Objects.equals(languages, that.languages) &&
                Objects.equals(contacts, that.contacts) &&
                Objects.equals(other, that.other) &&
                Objects.equals(location, that.location) &&
                Objects.equals(website, that.website) &&
                Objects.equals(status, that.status) &&
                Objects.equals(skills, that.skills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bio, interests, birthDate, languages, contacts, other, location, website, status, skills);
    }

    @Override
    public String toString() {
        return "UserDetailsExtended{" +
                "bio='" + bio + '\'' +
                ", interests=" + interests +
                ", birthDate=" + birthDate +
                ", languages=" + languages +
                ", contacts=" + contacts +
                ", other=" + other +
                ", location='" + location + '\'' +
                ", website='" + website + '\'' +
                ", status='" + status + '\'' +
                ", skills=" + skills +
                '}';
    }

    // --- JPA Converter ---
    @Converter
    public static class ConverterImpl implements AttributeConverter<UserDetailsExtended, String> {
        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(UserDetailsExtended attribute) {
            if (attribute == null) return null;
            try {
                return mapper.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize UserDetailsExtended to JSON", e);
            }
        }

        @Override
        public UserDetailsExtended convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) return null;
            try {
                return mapper.readValue(dbData, UserDetailsExtended.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to deserialize JSON to UserDetailsExtended", e);
            }
        }
    }
}
