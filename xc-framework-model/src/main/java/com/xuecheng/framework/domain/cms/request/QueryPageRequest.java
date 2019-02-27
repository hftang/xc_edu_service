package com.xuecheng.framework.domain.cms.request;

import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hftang
 * @date 2019-02-27 10:18
 * @desc 响应结果类型，分页查询统一使用QueryResponseResult
 */
@Data
public class QueryPageRequest extends ResponseResult {

    //站点id
    @ApiModelProperty("站点id")
    private String siteId;
    //页面ID
    @ApiModelProperty("页面ID")
    private String pageId;
    //页面名称
    @ApiModelProperty("页面名称")
    private String pageName;
    //别名
    @ApiModelProperty("页面别名")
    private String pageAliase;
    //模版id
    @ApiModelProperty("模版id")
    private String templateId;
}
