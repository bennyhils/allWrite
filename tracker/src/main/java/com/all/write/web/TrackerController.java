package com.all.write.web;

import com.all.write.NetworkMember;
import com.all.write.api.TrackerAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TrackerController implements TrackerAPI {

    @Autowired
    private NetworkMemberDao networkMemberDao;

    @Override
    @ResponseBody
    @RequestMapping(value = "/tracker/list", method = RequestMethod.POST)
    public List<NetworkMember> memberList(@RequestBody NetworkMember me) {
        networkMemberDao.add(me);
        return networkMemberDao.list();
    }

}
