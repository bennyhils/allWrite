package com.all.write.core;


import com.all.write.NetworkMember;
import com.all.write.api.Block;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class DataHolder {
    private Map<String, NetworkMember> networkMembers = new LinkedHashMap<>();
    private LinkedList<Block> blocks = new LinkedList<Block>();

    public Map<String, NetworkMember> getAllNetworkMembers() {
        return networkMembers;
    }

    public void setNetworkMembers(Map<String, NetworkMember> networkMembers) {
        this.networkMembers = networkMembers;
    }

    public void addBlock(Block block) {
        blocks.add(block);
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks.clear();
        this.blocks.addAll(blocks);
    }

    public Block lastBlock() {
        return blocks.getLast();
    }

}
