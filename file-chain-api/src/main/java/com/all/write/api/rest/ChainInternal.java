package com.all.write.api.rest;

import com.all.write.NetworkMember;
import com.all.write.api.FileDto;
import com.all.write.api.RequestingFileInfo;

import java.util.List;

public interface ChainInternal {
    Response list(String filter);
    void upload(String fileLocalPath, NetworkMember targetExternalAddress);
    List<RequestingFileInfo> listRequests();
    void download(RequestingFileInfo fileInfo, String localFilePath);
    List<FileDto> getOutgoingFiles();
    List<FileDto> getIncomingFiles();
}
