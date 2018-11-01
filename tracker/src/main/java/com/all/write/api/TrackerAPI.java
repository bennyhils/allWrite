package com.all.write.api;

import com.all.write.NetworkMember;

import java.util.List;

public interface TrackerAPI {

    List<NetworkMember> memberList(NetworkMember me);
}
