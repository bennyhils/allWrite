package com.all.write.api;

import com.all.write.NetworkMember;
import com.all.write.api.rest.Response;

public interface TrackerAPI {

    Response memberList(NetworkMember me);
}
