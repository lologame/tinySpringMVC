package com.lo.tinymvc.bean.base;

import java.util.Set;

/**
 * Created by Administrator on 2017/2/7.
 */
public interface ClassAnnotatedTypeMetaData extends AnnotatedTypeMetaData {

    public Set<MethodMetaData> getMethodMetadataSet();
    public void addMethodMetadata(MethodMetaData methodMetaData);
}
