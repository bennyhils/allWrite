package com.all.write.core;


import com.all.write.NetworkMember;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataHolder {
    private List<NetworkMember> networkMembers = new ArrayList<NetworkMember>();

    public List<NetworkMember> getAllNetworkMembers() {
        return networkMembers;
    }

    public void setNetworkMembers(List<NetworkMember> networkMembers) {
        this.networkMembers = networkMembers;
    }


}
