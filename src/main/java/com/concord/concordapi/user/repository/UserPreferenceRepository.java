package com.concord.concordapi.user.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.entity.UserPreference;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {   
    Optional<UserPreference> findByUserUsernameAndPreferenceKey(String username, String preferenceKey);
    List<UserPreference> findByUser(User user);
}
