package com.all.write.core;

import com.all.write.api.RequestingFileInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component
public class StateHolder {
    private List<RequestingFileInfo> requestingFileInfos = new ArrayList<>();
    private HashMap<String, RequestingFileInfo> outgoingRequest = new HashMap<>();

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

    public RequestingFileInfo getOutgoingRequest(String fileHash) {
        return outgoingRequest.get(fileHash);
    }
}
