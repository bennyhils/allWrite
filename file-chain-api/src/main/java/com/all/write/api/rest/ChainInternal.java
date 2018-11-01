package com.all.write.api.rest;

import com.all.write.NetworkMember;
import com.all.write.api.FileDto;
import com.all.write.api.RequestingFileInfo;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ChainInternal {
    ResponseEntity list(String filter);
    void uploadRequest(String fileLocalPath, NetworkMember targetExternalAddress);
    List<RequestingFileInfo> listRequests();
    void download(String localFilePath, RequestingFileInfo fileInfo);
    List<FileDto> getOutgoingFiles();
    List<FileDto> getIncomingFiles();
    List<FileDto> getOutgoingFilesHistory();
    List<FileDto> getIncomingFilesHistory();
}
