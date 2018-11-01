package com.all.write.web;

import com.all.write.NetworkMember;
import com.all.write.api.FileDto;
import com.all.write.api.RequestingFileInfo;
import com.all.write.api.rest.ChainInternal;
import org.springframework.stereotype.Controller;

import java.util.List;
@Controller
public class ClientInternalController implements ChainInternal {
    @Override
    public List<NetworkMember> list(String filter) {
        return null;
    }

    @Override
    public void upload(String fileLocalPath, NetworkMember targetExternalAddress) {

    }

    @Override
    public List<RequestingFileInfo> listRequests() {
        return null;
    }

    @Override
    public void download(RequestingFileInfo fileInfo, String localFilePath) {

    }

    @Override
    public List<FileDto> getOutgoingFiles() {
        return null;
    }

    @Override
    public List<FileDto> getIncomingFiles() {
        return null;
    }
}
