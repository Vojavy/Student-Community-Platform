package com.vojavy.AlmAgoraHub.repository;

import com.vojavy.AlmAgoraHub.model.user.UserProfileView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileViewRepository extends JpaRepository<UserProfileView, Long> {
    Optional<UserProfileView> findByUserId(Long userId);

    @Query(
        value = """
                SELECT *
                FROM user_profile_view v
                WHERE (:name   IS NULL OR :name   = '' OR v.jmeno    ILIKE '%'||:name||'%'
                                       OR v.prijmeni ILIKE '%'||:name||'%')
                AND (:email  IS NULL OR :email  = '' OR v.email    ILIKE '%'||:email||'%')
                AND (:domain IS NULL OR :domain = '' OR v.domain   ILIKE '%'||:domain||'%')
                AND (:rocnik IS NULL OR :rocnik = '' OR v.rocnik   ILIKE '%'||:rocnik||'%')
                AND (:titul  IS NULL OR :titul  = '' OR v.titul_pred ILIKE '%'||:titul||'%'
                                       OR v.titul_za   ILIKE '%'||:titul||'%')
                AND (:fakulta IS NULL OR :fakulta = '' OR v.fakulta_sp ILIKE '%'||:fakulta||'%')
                AND (:obor   IS NULL OR :obor   = '' OR v.obor_komb ILIKE '%'||:obor||'%')
                ORDER BY (v.domain IS NOT NULL) DESC, v.prijmeni, v.jmeno
            """,
            countQuery = """
                SELECT count(*)
                FROM user_profile_view v
                WHERE (:name     IS NULL OR v.jmeno      ILIKE '%'||:name||'%'
                                 OR v.prijmeni   ILIKE '%'||:name||'%')
                AND (:email    IS NULL OR v.email      ILIKE '%'||:email||'%')
                AND (:domain   IS NULL OR v.domain     ILIKE '%'||:domain||'%')
                AND (:rocnik   IS NULL OR v.rocnik     ILIKE '%'||:rocnik||'%')
                AND (:titul    IS NULL OR v.titul_pred ILIKE '%'||:titul||'%'
                                 OR v.titul_za   ILIKE '%'||:titul||'%')
                AND (:fakulta  IS NULL OR v.fakulta_sp ILIKE '%'||:fakulta||'%')
                AND (:obor     IS NULL OR v.obor_komb  ILIKE '%'||:obor||'%')
            """,
        nativeQuery = true
    )
    Page<UserProfileView> search(
            @Param("name")   String name,
            @Param("email")  String email,
            @Param("domain") String domain,
            @Param("rocnik") String rocnik,
            @Param("titul")  String titul,
            @Param("fakulta")String fakulta,
            @Param("obor")   String obor,
            Pageable pageable
    );
}
