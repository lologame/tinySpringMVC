package com.lo.test.service;

/**
 * Created by Administrator on 2017/2/7.
 */
public class HelloWorldServiceImpl implements HelloWorldService{
    private String text;

    private OutputService outputService;

    @Override
    public void helloWorld(){
        outputService.output(text);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOutputService(OutputService outputService) {
        this.outputService = outputService;
    }
}
