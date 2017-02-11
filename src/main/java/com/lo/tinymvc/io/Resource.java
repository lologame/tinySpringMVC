package com.lo.tinymvc.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/2/6.
 */
public interface Resource {
    InputStream getInputStream() throws IOException;
}
