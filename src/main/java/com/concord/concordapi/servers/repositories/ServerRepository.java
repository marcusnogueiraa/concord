package com.concord.concordapi.servers.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concord.concordapi.servers.entities.Server;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long>{
    
}
