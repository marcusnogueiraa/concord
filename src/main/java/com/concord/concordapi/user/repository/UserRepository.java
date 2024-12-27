package com.concord.concordapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concord.concordapi.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {   

}
