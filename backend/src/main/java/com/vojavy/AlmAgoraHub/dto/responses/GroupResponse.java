package com.vojavy.AlmAgoraHub.dto.responses;

import com.vojavy.AlmAgoraHub.model.group.Group;
import com.vojavy.AlmAgoraHub.model.UniversityDomain;

/**
 * DTO для вывода информации о группе.
 */
public class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private String topics;    // JSONB как строка
    private String domain;    // код домена (например UniversityDomain.getDomain())
    private boolean isPublic;

    public GroupResponse() {}

    public GroupResponse(Long id,
                         String name,
                         String description,
                         String topics,
                         String domain,
                         boolean isPublic) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.topics = topics;
        this.domain = domain;
        this.isPublic = isPublic;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTopics() {
        return topics;
    }

    public String getDomain() {
        return domain;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    /**
     * Утилитный метод для преобразования JPA-сущности Group в DTO.
     */
    public static GroupResponse fromEntity(Group g) {
        String domainCode = null;
        UniversityDomain d = g.getDomain();
        if (d != null) {
            domainCode = d.getDomain();      // или d.getDomainName(), по вашим требованиям
        }
        return new GroupResponse(
                g.getId(),
                g.getName(),
                g.getDescription(),
                g.getTopics(),
                domainCode,
                g.isPublic()
        );
    }
}
