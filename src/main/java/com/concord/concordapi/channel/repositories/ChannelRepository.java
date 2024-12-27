package com.concord.concordapi.channel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concord.concordapi.channel.entities.Channel;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long>{
    
}
