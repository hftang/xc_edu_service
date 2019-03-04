package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @author hftang
 * @date 2019-03-04 10:10
 * @desc 封装一下自定义的异常类
 */
public class ExceptionCast {

    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
