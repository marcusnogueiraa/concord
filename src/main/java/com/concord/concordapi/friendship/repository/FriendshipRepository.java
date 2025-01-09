package com.concord.concordapi.friendship.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.concord.concordapi.friendship.entity.Friendship;
import com.concord.concordapi.friendship.entity.FriendshipStatus;
import com.concord.concordapi.user.entity.User;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f FROM Friendship f WHERE (f.from_user = :user OR f.to_user = :user) AND f.status = :status")
    List<Friendship> findAllFriendshipsByUserAndStatus(User user, FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.from_user = :fromUser AND f.to_user = :toUser) AND f.status = :status")
    List<Friendship> findFriendshipsUsers(User fromUser, User toUser, FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE ((f.from_user = :user1 AND f.to_user = :user2) OR (f.from_user = :user2 AND f.to_user = :user1)) AND f.status = :status")
    List<Friendship> findFriendshipsBetweenUsers(User user1, User user2, FriendshipStatus status);
}