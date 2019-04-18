package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author hftang
 * @date 2019-03-26 17:22
 * @desc 媒资文件的管理
 */
@Api(value = "媒资文件管理", description = "媒体文件的管理")
public interface MediaFileControllerApi {

    @ApiOperation("查询文件列表")
    public QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest);


}