package com.all.write.api;

import com.all.write.NetworkMember;
import org.springframework.http.ResponseEntity;

public interface TrackerAPI {

    ResponseEntity memberList(NetworkMember me);
}
