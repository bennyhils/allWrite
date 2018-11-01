package com.all.write.api.rest;

import com.all.write.api.ExternalAddress;
import com.all.write.api.FileDto;
import com.all.write.api.RequestingFileInfo;
import java.util.List;

public interface ChainInternal {
    List<ExternalAddress> list(String filter);
    void upload(String fileLocalPath, ExternalAddress targetExternalAddress);
    List<RequestingFileInfo> listRequests();
    void download(RequestingFileInfo fileInfo, String localFilePath);
    List<FileDto> getOutgoingFiles();
    List<FileDto> getIncomingFiles();
}
