package com.all.write.core;

import com.all.write.api.RequestingFileInfo;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StateHolder {
    private List<RequestingFileInfo> requestingFileInfos = new ArrayList<>();
    private HashMap<String, RequestingFileInfo> outgoingRequest = new HashMap<>();
    private HashMap<String, byte []> filesSecretKeys = new HashMap<String, byte []>();

    public List<RequestingFileInfo> getRequestingFileInfos() {
        return requestingFileInfos;
    }

    public void addRequestingFileInfo(RequestingFileInfo requestingFileInfo) {
        requestingFileInfos.add(requestingFileInfo);
    }

    public boolean removeRequestingFileInfo(RequestingFileInfo requestingFileInfoForDelete){
        Iterator<RequestingFileInfo> iterator = requestingFileInfos.iterator();
        while (iterator.hasNext()){
            RequestingFileInfo requestingFileInfo = iterator.next();
            if (requestingFileInfo.equals(requestingFileInfoForDelete)){
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public void addOutgoingFiles(RequestingFileInfo requestingFileInfo) {
        outgoingRequest.put(requestingFileInfo.getHash(), requestingFileInfo);
    }

    public void addFileSecretKey(String fileInfoHash, byte [] secretKey) {
        filesSecretKeys.put(fileInfoHash, secretKey);
    }

    public byte [] getSecretKey(String fileInfoHash) {
        return filesSecretKeys.get(fileInfoHash);
    }

    public RequestingFileInfo getOutgoingRequest(String fileHash) {
        return outgoingRequest.get(fileHash);
    }

    public Collection<RequestingFileInfo> getOutgoingRequests() {
        return outgoingRequest.values();
    }
}
