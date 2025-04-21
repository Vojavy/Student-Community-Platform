package com.vojavy.AlmAgoraHub.repository.group;

import com.vojavy.AlmAgoraHub.model.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    @Query(value = """
        SELECT *
          FROM groups g
         WHERE (:name      IS NULL OR g.name      ILIKE CONCAT('%', :name, '%'))
           AND (:domainId  IS NULL OR g.domain_id = :domainId)
           AND (:isPublic  IS NULL OR g.is_public = :isPublic)
           AND (
                 :#{#topics == null} = true
              OR jsonb_exists_any(g.topics, :topics)
           )
        """,
            nativeQuery = true)
    List<Group> findByFilters(
            @Param("name")     String name,
            @Param("domainId") Long domainId,
            @Param("isPublic") Boolean isPublic,
            @Param("topics")   String[] topics
    );

    @Query(value = """
      SELECT *
        FROM groups g
       WHERE (:domainId IS NULL    OR g.domain_id = :domainId)
         AND (:topics   IS NULL    OR jsonb_exists_any(g.topics, :topics))
  """, nativeQuery = true)
    List<Group> findByDomainIdAndTopics(
            @Param("domainId") Long domainId,
            @Param("topics")   String[] topics
    );
}
