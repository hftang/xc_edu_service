package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @author hftang
 * @date 2019-03-04 10:08
 * @desc 全局的已知的异常处理类
 *  继承runtimeException 是为代码入侵性少
 */
public class CustomException extends RuntimeException {

    private ResultCode resultCode;

    public CustomException(ResultCode code){
        this.resultCode=code;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
