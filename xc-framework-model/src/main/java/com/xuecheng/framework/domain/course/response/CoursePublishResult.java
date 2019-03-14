package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author hftang
 * @date 2019-03-13 17:44
 * @desc
 */

@NoArgsConstructor
@Data
@ToString
public class CoursePublishResult extends ResponseResult {
    private String pageUrl;

    public CoursePublishResult(ResultCode resultCode, String pageUrl) {
        super(resultCode);
        this.pageUrl = pageUrl;
    }
}
