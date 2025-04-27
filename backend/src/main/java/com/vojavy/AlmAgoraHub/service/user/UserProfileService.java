package com.vojavy.AlmAgoraHub.service.user;

import com.vojavy.AlmAgoraHub.model.user.UserProfileView;
import com.vojavy.AlmAgoraHub.repository.UserProfileViewRepository;
import com.vojavy.AlmAgoraHub.dto.responses.UserProfileResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileViewRepository profileViewRepository;

    public UserProfileService(UserProfileViewRepository profileViewRepository) {
        this.profileViewRepository = profileViewRepository;
    }

    public Optional<UserProfileResponse> getProfileById(Long userId) {
        return profileViewRepository.findByUserId(userId).map(this::mapToDto);
    }

    private UserProfileResponse mapToDto(UserProfileView entity) {
        UserProfileResponse dto = new UserProfileResponse();

        dto.setUserId(entity.getUserId());
        dto.setEmail(entity.getEmail());
        dto.setActive(entity.getActive());
        dto.setDomain(entity.getDomain());
        dto.setAdminEmail(entity.getAdminEmail());
        dto.setDetails(entity.getDetails());
        dto.setOsCislo(entity.getOsCislo());
        dto.setStprIdno(entity.getStprIdno());
        dto.setUserName(entity.getUserName());
        dto.setJmeno(entity.getJmeno());
        dto.setPrijmeni(entity.getPrijmeni());
        dto.setTitulPred(entity.getTitulPred());
        dto.setTitulZa(entity.getTitulZa());
        dto.setPohlavi(entity.getPohlavi());
        dto.setFakultaSp(entity.getFakultaSp());
        dto.setOborKomb(entity.getOborKomb());
        dto.setNazevSp(entity.getNazevSp());
        dto.setKodSp(entity.getKodSp());
        dto.setFormaSp(entity.getFormaSp());
        dto.setTypSp(entity.getTypSp());
        dto.setRocnik(entity.getRocnik());
        dto.setStav(entity.getStav());
        dto.setMistoVyuky(entity.getMistoVyuky());
        dto.setCisloKarty(entity.getCisloKarty());
        dto.setRozvrhovyKrouzek(entity.getRozvrhovyKrouzek());
        dto.setStudijniKruh(entity.getStudijniKruh());
        dto.setEvidovanBankovniUcet(entity.getEvidovanBankovniUcet());

        return dto;
    }
}
