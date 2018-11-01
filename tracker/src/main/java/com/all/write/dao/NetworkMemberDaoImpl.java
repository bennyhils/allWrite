package com.all.write.dao;

import com.all.write.NetworkMember;
import com.all.write.web.NetworkMemberDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class NetworkMemberDaoImpl implements NetworkMemberDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkMemberDaoImpl.class);

    private final LinkedHashMap<String, NetworkMember> repo = new LinkedHashMap<>();

    @Override
    public List<NetworkMember> list() {
        return new ArrayList<>(repo.values());
    }

    @Override
    public void add(NetworkMember networkMember) {
        if(repo.putIfAbsent(networkMember.getPublicKey(), networkMember) != null) {
            LOGGER.error("already exist");
        }
    }
}
