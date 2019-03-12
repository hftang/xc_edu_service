package com.xuecheng.api.system;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author hftang
 * @date 2019-03-08 10:36
 * @desc
 */

@Api(value = "数据字典接口",description = "提供数据字典的接口管理，查询功能")
public interface SysDicthinaryControllerApi {

    @ApiOperation("数据字典查询接口")
    public SysDictionary getByType(String type);
}
