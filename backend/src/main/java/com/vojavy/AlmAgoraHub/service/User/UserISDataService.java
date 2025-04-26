package com.vojavy.AlmAgoraHub.service.User;

import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.model.user.UserISData;
import com.vojavy.AlmAgoraHub.repository.UserISDataRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserISDataService {
    private final UserISDataRepository userISDataRepository;

    public UserISDataService(UserISDataRepository userISDataRepository) {
        this.userISDataRepository = userISDataRepository;
    }

    public Optional<UserISData> getByUser(User user) {
        return userISDataRepository.findByUser(user);
    }

    public Optional<UserISData> getByUserId(Long userId) {
        return userISDataRepository.findByUserId(userId);
    }

    public UserISData save(UserISData data) {
        return userISDataRepository.save(data);
    }

    public void delete(UserISData data) {
        userISDataRepository.delete(data);
    }

    public void deleteByUser(User user) {
        userISDataRepository.findByUser(user).ifPresent(userISDataRepository::delete);
    }
}
