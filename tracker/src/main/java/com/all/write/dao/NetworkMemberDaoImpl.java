package com.all.write.dao;

import com.all.write.NetworkMember;
import com.all.write.web.NetworkMemberDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class NetworkMemberDaoImpl implements NetworkMemberDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkMemberDaoImpl.class);

    private final LinkedList<NetworkMember> repo = new LinkedList<>();

    @Override
    public synchronized List<NetworkMember> list() {
        return repo;
    }

    @Override
    public synchronized void remove(NetworkMember networkMember) {
        repo.remove(networkMember);

        LOGGER.info("Network member removed {}", networkMember);
    }

    @Override
    public synchronized void add(NetworkMember networkMember) {
        if (repo.stream().anyMatch(v -> v.getAddress().equals(networkMember.getAddress()))) {
            LOGGER.warn("address already bind {}", networkMember);
            return;
        }
        repo.add(networkMember);
        LOGGER.info("Network member successfully added {}, current member count {}",
                networkMember, repo.size());
    }
}
