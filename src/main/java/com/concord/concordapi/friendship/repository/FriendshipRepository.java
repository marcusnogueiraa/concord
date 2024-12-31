package com.concord.concordapi.friendship.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.concord.concordapi.friendship.entity.Friendship;
import com.concord.concordapi.user.entity.User;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f WHERE f.from_user = :user OR f.to_user = :user")
    List<Friendship> findAllByUser(User user);
}