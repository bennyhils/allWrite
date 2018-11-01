package com.all.write.core;


import com.all.write.NetworkMember;
import com.all.write.api.Block;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class DataHolder {
    private List<NetworkMember> networkMembers = new ArrayList<NetworkMember>();
    private List<Block> blocks = new LinkedList<Block>();

    public List<NetworkMember> getAllNetworkMembers() {
        return networkMembers;
    }

    public void setNetworkMembers(List<NetworkMember> networkMembers) {
        this.networkMembers = networkMembers;
    }

    public void addBlock(Block block) {
        blocks.add(block);
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

}
