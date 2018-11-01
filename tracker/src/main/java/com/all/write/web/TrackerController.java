package com.all.write.web;

import com.all.write.NetworkMember;
import com.all.write.api.TrackerAPI;
import com.all.write.api.rest.Response;
import com.all.write.service.MemberChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TrackerController implements TrackerAPI {

    @Autowired
    private NetworkMemberDao networkMemberDao;

    @Autowired
    private MemberChecker memberChecker;

    @Override
    @ResponseBody
    @RequestMapping(value = "/tracker/list", method = RequestMethod.POST)
    public Response memberList(@RequestBody NetworkMember me) {

        //async ping
        if (!memberChecker.pingMember(me)) {
            throw new RuntimeException("Ping member failed! Member: " + me);
        }

        // and sync addition
        networkMemberDao.add(me);
        Response response = new Response();
        response.getData().put("list", networkMemberDao.list());
        return response;
    }

}
