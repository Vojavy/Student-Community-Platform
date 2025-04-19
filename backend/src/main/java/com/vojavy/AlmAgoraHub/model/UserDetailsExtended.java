package com.vojavy.AlmAgoraHub.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsExtended {
    private String bio;
    private List<String> interests;
    private LocalDate birthDate;
    private List<String> languages;
    private Map<String, String> contacts; // e.g. {"inst": "", "tg": "", "fb": "", ...}
    private Map<String, String> other;    // e.g. {"custom1": "text"}

    // Getters and Setters

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

    @Converter  // убрали autoApply = true
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
