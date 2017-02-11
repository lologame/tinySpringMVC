package com.lo.tinymvc.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by Administrator on 2017/2/6.
 */
public class UrlResouce implements Resource {
    private final URL url;

    public UrlResouce(URL url) {
        this.url = url;
    }

    @Override
    //根据URL载入输入流
    public InputStream getInputStream() throws IOException {
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        return urlConnection.getInputStream();
    }
}
