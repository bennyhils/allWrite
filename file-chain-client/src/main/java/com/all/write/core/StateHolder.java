package com.all.write.core;

import com.all.write.api.RequestingFileInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StateHolder {
    private List<RequestingFileInfo> requestingFileInfos;

    public List<RequestingFileInfo> getRequestingFileInfos() {
        return requestingFileInfos;
    }

    public void setRequestingFileInfos(List<RequestingFileInfo> requestingFileInfos) {
        this.requestingFileInfos = requestingFileInfos;
    }

    public void addRequestingFileInfo(RequestingFileInfo requestingFileInfo) {
        requestingFileInfos.add(requestingFileInfo);
    }
}
