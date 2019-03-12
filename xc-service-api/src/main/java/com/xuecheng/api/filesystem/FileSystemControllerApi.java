package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hftang
 * @date 2019-03-11 14:30
 * @desc
 */

@Api(value = "文件管理的接口",description = "文件管理接口，提供页面的增删改查")
public interface FileSystemControllerApi {

    @ApiOperation("上传文件接口")
    public UploadFileResult upload(MultipartFile multipartFile,
                                   String filetag,
                                   String businesskey,
                                   String metadata);
}
