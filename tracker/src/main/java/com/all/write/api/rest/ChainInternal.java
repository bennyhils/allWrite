package com.all.write.api.rest;

import com.all.write.api.ExternalAddress;
import com.all.write.api.FileInfo;

import java.util.List;

public interface ChainInternal {
    List<ExternalAddress> list(String filter);
    void upload(String fileLocalPath, ExternalAddress targetExternalAddress);
    List<FileInfo> listRequests();
    void download(FileInfo fileInfo, String localFilePath);
}
