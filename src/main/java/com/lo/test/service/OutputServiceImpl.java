package com.lo.test.service;

/**
 * Created by Administrator on 2017/2/7.
 */
public class OutputServiceImpl implements OutputService{
    @Override
    public void output(String text){
        System.out.println(text);
    }
}
