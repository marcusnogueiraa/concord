package com.concord.concordapi.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concord.concordapi.server.entity.Server;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long>{
    
}
