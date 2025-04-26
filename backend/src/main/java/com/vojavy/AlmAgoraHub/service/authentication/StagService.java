// src/main/java/com/vojavy/AlmAgoraHub/service/StagService.java
package com.vojavy.AlmAgoraHub.service.authentication;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

import com.vojavy.AlmAgoraHub.config.StagProperties;
import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.user.UserISData;
import com.vojavy.AlmAgoraHub.model.user.UserToken;
import com.vojavy.AlmAgoraHub.dto.responses.StagUserResponse;
import com.vojavy.AlmAgoraHub.dto.responses.StudentInfoResponse;

import com.vojavy.AlmAgoraHub.service.UniversityDomainService;
import com.vojavy.AlmAgoraHub.service.User.UserISDataService;
import com.vojavy.AlmAgoraHub.service.User.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StagService {

    private final StagProperties props;
    private final RestTemplate rest;
    private final UserTokenService tokenService;
    private final UserISDataService userISDataService;
    private final UserTokenService userTokenService;
    private final UniversityDomainService universityDomainService;
    private final UserService userService;

    @Autowired
    public StagService(
            StagProperties props,
            RestTemplate rest,
            UserTokenService tokenService,
            UserISDataService userISDataService,
            UserTokenService userTokenService,
            UniversityDomainService universityDomainService,
            UserService userService) {
        this.props = props;
        this.rest = rest;
        this.tokenService = tokenService;
        this.userISDataService = userISDataService;
        this.userTokenService = userTokenService;
        this.universityDomainService = universityDomainService;
        this.userService = userService;
    }

    @Transactional
    public void saveTokenAndFetchUserData(User user, String ticket, String longTicket, String domain) {
        boolean isLong = "1".equals(longTicket) || "true".equalsIgnoreCase(longTicket);
        Instant expiration = isLong
                ? Instant.now().plus(90, ChronoUnit.DAYS)
                : Instant.now().plus(30, ChronoUnit.MINUTES);
        userTokenService.saveUniversityToken(user.getId(), ticket, expiration, "stag");

        String osCislo = fetchOsCisloFromTicket(ticket, domain);
        StudentInfoResponse studentInfo = fetchStudentInfo(user.getId(), osCislo, domain);

        UniversityDomain universityDomain = universityDomainService
                .getDomainByCode(domain)
                .orElseThrow(() -> new RuntimeException("No such domain: " + domain));

        user.setDomain(universityDomain);
        userService.update(user);

        UserISData userISData = userISDataService.getByUser(user)
                .orElse(new UserISData());

        userISData.setUser(user);
        userISData.setOsCislo(studentInfo.getOsCislo());
        userISData.setStprIdno(studentInfo.getStprIdno());
        userISData.setUserName(studentInfo.getUserName());
        userISData.setEmail(studentInfo.getEmail());
        userISData.setJmeno(studentInfo.getJmeno());
        userISData.setPrijmeni(studentInfo.getPrijmeni());
        userISData.setTitulPred(studentInfo.getTitulPred());
        userISData.setTitulZa(studentInfo.getTitulZa());
        userISData.setPohlavi(studentInfo.getPohlavi());
        userISData.setOborKomb(studentInfo.getOborKomb());
        userISData.setNazevSp(studentInfo.getNazevSp());
        userISData.setKodSp(studentInfo.getKodSp());
        userISData.setFakultaSp(studentInfo.getFakultaSp());
        userISData.setFormaSp(studentInfo.getFormaSp());
        userISData.setTypSp(studentInfo.getTypSp());
        userISData.setRocnik(studentInfo.getRocnik());
        userISData.setStav(studentInfo.getStav());
        userISData.setMistoVyuky(studentInfo.getMistoVyuky());
        userISData.setCisloKarty(studentInfo.getCisloKarty());
        userISData.setRozvrhovyKrouzek(studentInfo.getRozvrhovyKrouzek());
        userISData.setStudijniKruh(studentInfo.getStudijniKruh());
        userISData.setEvidovanBankovniUcet(studentInfo.getEvidovanBankovniUcet());
        userISData.setUpdatedAt(Instant.now());

        if (userISData.getId() == null) {
            userISData.setCreatedAt(Instant.now());
        }

        userISDataService.save(userISData);
    }


    private String fetchOsCisloFromTicket(String ticket, String domain) {
        String url = props.getWsBaseUrlFirst() + domain + props.getWsBaseUrlSecond() +
                "/services/rest2/help/getStagUserListForLoginTicket?ticket=" + ticket + "&outputFormat=JSON";

        ResponseEntity<StagUserResponse> response = rest.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                StagUserResponse.class
        );

        if (response.getBody() != null &&
                response.getBody().getStagUserInfo() != null &&
                !response.getBody().getStagUserInfo().isEmpty()) {

            return response.getBody().getStagUserInfo().get(0).getOsCislo();
        }

        throw new IllegalStateException("No STAG user info or osCislo not found");
    }


    /**
     * Example test call: fetch student info from STAG using the stored token.
     */
    public StudentInfoResponse fetchStudentInfo(Long userId, String stuOsCislo, String domain) {
        Optional<UserToken> optionalUserToken = tokenService.getUniversityToken(userId);
        String ticket = optionalUserToken
                .orElseThrow(() -> new IllegalStateException("No STAG token for user"))
                .getToken();

        // STAG uses HTTP Basic where username=ticket, password empty
        String credentials = Base64.getEncoder()
                .encodeToString((ticket + ":").getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
        headers.setAccept(MediaType.parseMediaTypes("application/json"));

        String url = props.getWsBaseUrlFirst()+ domain + props.getWsBaseUrlSecond() +
                "/services/rest2/student/getStudentInfo?osCislo=" +
                URLEncoder.encode(stuOsCislo, StandardCharsets.UTF_8);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<StudentInfoResponse> resp = rest.exchange(
                url, HttpMethod.GET, request, StudentInfoResponse.class);

        return resp.getBody();
    }

    public void deleteStagToken(Long userId) {
        userTokenService.deleteUniToken(userId);
    }
}
