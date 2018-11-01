package com.all.write.web;

import com.all.write.NetworkMember;
import com.all.write.api.FileDto;
import com.all.write.api.RequestingFileInfo;
import com.all.write.api.rest.ChainInternal;
import com.all.write.core.DataHolder;
import com.all.write.core.StateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
@Controller
public class ClientInternalController implements ChainInternal {

    @Autowired
    private DataHolder dataHolder;

    @Autowired
    private StateHolder stateHolder;

    @Override
    @RequestMapping(value = "/member/list", method = RequestMethod.GET)
    public List<NetworkMember> list(String filter) {
        return dataHolder.getAllNetworkMembers();
    }

    @Override
    public void upload(String fileLocalPath, NetworkMember targetExternalAddress) {

    }

    @Override
    @RequestMapping(value = "/requests/list", method = RequestMethod.GET)
    public List<RequestingFileInfo> listRequests() {
        return stateHolder.getRequestingFileInfos();
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
