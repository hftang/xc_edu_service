package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author hftang
 * @date 2019-03-13 17:00
 * @desc 一键发布
 */
@NoArgsConstructor
@Data
@ToString
public class CmsPostPageResult extends ResponseResult {

    private String pageUrl;



    public CmsPostPageResult(ResultCode resultCode, String pageUrl) {
        super(resultCode);
        this.pageUrl = pageUrl;
    }
}
