package com.lo.tinymvc.mvc;

/**
 * Created by Administrator on 2017/2/8.
 */
public class ModelAndView {

    private String view;

    private ModelMap model;

    public ModelAndView(){}

    public ModelAndView(String view,ModelMap model){
        this.view = view;
        this.model = model;
    }

    public String getView() {
        return view;
    }

    public ModelMap getModel() {
        return model;
    }

}
