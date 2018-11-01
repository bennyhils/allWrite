package com.all.write.web;

import com.all.write.NetworkMember;

import java.util.List;

public interface NetworkMemberDao {

    List<NetworkMember> list();
    void add(NetworkMember networkMember);

}
