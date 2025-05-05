package com.vojavy.AlmAgoraHub.service.user;

import com.vojavy.AlmAgoraHub.model.user.UserProfileView;
import com.vojavy.AlmAgoraHub.repository.UserProfileViewRepository;
import com.vojavy.AlmAgoraHub.dto.responses.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<UserProfileResponse> searchProfiles(
            String name,
            String email,
            String domain,
            String rocnik,
            String titul,
            String fakulta,
            String obor,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return profileViewRepository
                .search(name, email, domain, rocnik, titul, fakulta, obor, pageable)
                .map(this::mapToDtoForSearch);
    }

    private UserProfileResponse mapToDtoForSearch(UserProfileView v) {
        UserProfileResponse dto = new UserProfileResponse();

        // —————— Обязательные ID/контакты ——————
        dto.setUserId(v.getUserId());
        dto.setEmail(v.getEmail());
        dto.setDomain(v.getDomain());
        dto.setJmeno(v.getJmeno());
        dto.setPrijmeni(v.getPrijmeni());
        dto.setDetails(v.getDetails());
        dto.setTitulPred(v.getTitulPred());
        dto.setTitulZa(v.getTitulZa());
        dto.setPohlavi(v.getPohlavi());
        dto.setFakultaSp(v.getFakultaSp());
        dto.setOborKomb(v.getOborKomb());
        dto.setNazevSp(v.getNazevSp());
        dto.setKodSp(v.getKodSp());
        dto.setFormaSp(v.getFormaSp());
        dto.setTypSp(v.getTypSp());
        dto.setRocnik(v.getRocnik());
        dto.setStav(v.getStav());
        dto.setMistoVyuky(v.getMistoVyuky());

        return dto;
    }
}
