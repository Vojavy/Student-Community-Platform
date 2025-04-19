DROP VIEW IF EXISTS pending_friend_requests;
DROP VIEW IF EXISTS user_profile_view;

ALTER TABLE user_friends
ALTER COLUMN status TYPE VARCHAR(255);

CREATE VIEW pending_friend_requests AS
SELECT uf.user_id,
       uf.friend_id,
       u.email AS friend_email,
       ud.domain AS friend_domain,
       uf.status
FROM user_friends uf
         JOIN users u ON uf.friend_id = u.id
         LEFT JOIN university_domains ud ON u.domain_id = ud.id
WHERE uf.status = 'pending' AND uf.hidden = FALSE;

CREATE VIEW user_profile_view
            (user_id, email, active, details, domain, admin_email, os_cislo, stpr_idno, user_name, jmeno, prijmeni, titul_pred,
             titul_za, pohlavi, fakulta_sp, obor_komb, nazev_sp, kod_sp, forma_sp, typ_sp, rocnik, stav, misto_vyuky,
             cislo_karty, rozvrhovy_krouzek, studijni_kruh, evidovan_bankovni_ucet)
AS
SELECT u.id AS user_id,
       u.email,
       u.active,
       u.details,
       d.domain,
       d.admin_email,
       isd.os_cislo,
       isd.stpr_idno,
       isd.user_name,
       isd.jmeno,
       isd.prijmeni,
       isd.titul_pred,
       isd.titul_za,
       isd.pohlavi,
       isd.fakulta_sp,
       isd.obor_komb,
       isd.nazev_sp,
       isd.kod_sp,
       isd.forma_sp,
       isd.typ_sp,
       isd.rocnik,
       isd.stav,
       isd.misto_vyuky,
       isd.cislo_karty,
       isd.rozvrhovy_krouzek,
       isd.studijni_kruh,
       isd.evidovan_bankovni_ucet
FROM users u
         LEFT JOIN university_domains d ON u.domain_id = d.id
         LEFT JOIN user_is_data isd ON u.id = isd.user_id;

ALTER VIEW user_profile_view
    OWNER TO admin;
