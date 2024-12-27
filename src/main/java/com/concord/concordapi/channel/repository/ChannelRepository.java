package com.concord.concordapi.channel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concord.concordapi.channel.entity.Channel;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long>{
    
}
