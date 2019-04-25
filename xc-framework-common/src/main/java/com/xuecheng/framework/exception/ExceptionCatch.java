package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author hftang
 * @date 2019-03-04 10:13
 * @desc 控制器增强 拦截指定类型的异常拦截
 */

@ControllerAdvice
public class ExceptionCatch {

    private static final Logger logger= LoggerFactory.getLogger(ExceptionCatch.class);

    //告诉拦截什么类型的异常类
    //返回类型是json类型

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException e){
        e.printStackTrace();
        logger.error("catch exception :::"+e.getResultCode());

        ResultCode resultCode = e.getResultCode();

        ResponseResult responseResult = new ResponseResult(resultCode);

        return responseResult;

    }
}
