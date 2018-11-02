package com.all.write.web;

import com.all.write.NetworkMember;
import com.all.write.api.TrackerAPI;
import com.all.write.service.MemberChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TrackerController implements TrackerAPI {

    @Autowired
    private NetworkMemberDao networkMemberDao;

    @Autowired
    private MemberChecker memberChecker;

    @ResponseBody
    @RequestMapping(value = "/tracker/register", method = RequestMethod.POST)
    public ResponseEntity memberRegister(@RequestBody NetworkMember me) {

        //async ping
        if (!memberChecker.pingMember(me)) {
            throw new RuntimeException("Ping member failed! Member: " + me);
        }

        // and sync addition
        networkMemberDao.add(me);
        return new ResponseEntity<>(
                networkMemberDao.list().toArray(),
                HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/tracker/unregister", method = RequestMethod.POST)
    public void memberUnregister(@RequestBody NetworkMember me) {
        // and sync addition
        networkMemberDao.remove(me);
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/tracker/list", method = RequestMethod.POST)
    public ResponseEntity memberList(@RequestBody NetworkMember me) {

        //async ping
        if (!memberChecker.pingMember(me)) {
            throw new RuntimeException("Ping member failed! Member: " + me);
        }

        return new ResponseEntity<>(
                networkMemberDao.list().toArray(),
                HttpStatus.OK);
    }

}
