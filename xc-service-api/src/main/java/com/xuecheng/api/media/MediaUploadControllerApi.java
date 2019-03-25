package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hftang
 * @date 2019-03-25 14:56
 * @desc 媒体管理
 */

@Api(value = "媒资管理接口",description = "媒资管理接口")
public interface MediaUploadControllerApi {

    @ApiOperation("文件上传注册 校验文件是否存在")
    public ResponseResult register(String fileMd5,String fileName,Long fileSize,String mimetype,String fileExt);

    //校验分块是否存在

    @ApiOperation(value = "校验分块是否存在")
    public CheckChunkResult checkChunk(String fileMd5,Integer chunk,Integer chunkSize);

    //上传分块
    @ApiOperation("上传分块")
    public ResponseResult uploadChunk(MultipartFile file,String fileMd5, Integer chunk);

    //合并分块
    @ApiOperation("合并分块")
    public ResponseResult mergeChunks(String fileMd5,String fileName,Long fileSize,String mimetype,String fileExt);


}
