package com.lo.test.controller;

import com.lo.test.beans.User;
import com.lo.test.service.UserService;
import com.lo.tinymvc.annotation.Autowired;
import com.lo.tinymvc.annotation.RequestMapping;
import com.lo.tinymvc.annotation.Controller;
import com.lo.tinymvc.mvc.ModelMap;

/**
 * Created by Administrator on 2017/2/7.
 */

@Controller("user")
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService service;

    @RequestMapping("/login")
    public String login(User user , ModelMap model){
        model.addAttribute("newUser",user);
        return service.checkLogin(user);
    }
}
