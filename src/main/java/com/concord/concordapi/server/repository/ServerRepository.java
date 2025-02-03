package com.concord.concordapi.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.user.entity.User;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long>{
    @Query("SELECT s FROM Server s WHERE s.owner = :owner")
    List<Server> findByOwner(@Param("owner") User owner);
}
