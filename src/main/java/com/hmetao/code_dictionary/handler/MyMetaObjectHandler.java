package com.hmetao.code_dictionary.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        if (this.getFieldValByName("createTime", metaObject) == null)
            this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);

        if (this.getFieldValByName("modifyTime", metaObject) == null)
            this.setFieldValByName("modifyTime", LocalDateTime.now(), metaObject);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("modifyTime", LocalDateTime.now(), metaObject);
    }
}
