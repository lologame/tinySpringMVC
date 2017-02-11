package com.lo.test.service;

import com.lo.test.beans.User;
import com.lo.tinymvc.annotation.Service;

/**
 * Created by Administrator on 2017/2/7.
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public String checkLogin(User user) {
        if(user.getUsername().equals("lo") && user.getPassword().equals("lo")){
            return "success";
        }
        return "error";
    }
}
