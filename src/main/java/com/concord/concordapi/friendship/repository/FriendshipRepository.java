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

    @Query("SELECT f FROM Friendship f WHERE (f.fromUser = :user OR f.toUser = :user) AND f.status = :status")
    List<Friendship> findAllFriendshipsByUserAndStatus(User user, FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.fromUser = :fromUser AND f.toUser = :toUser) AND f.status = :status")
    List<Friendship> findFriendshipsUsers(User fromUser, User toUser, FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE ((f.fromUser = :user1 AND f.toUser = :user2) OR (f.fromUser = :user2 AND f.toUser = :user1)) AND f.status = :status")
    List<Friendship> findFriendshipsBetweenUsers(User user1, User user2, FriendshipStatus status);
}