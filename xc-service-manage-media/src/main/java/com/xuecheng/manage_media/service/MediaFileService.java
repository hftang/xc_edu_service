package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hftang
 * @date 2019-03-26 17:30
 * @desc
 */
@Service
public class MediaFileService {

    @Autowired
    MediaFileRepository mediaFileRepository;


    //查询媒资列表
    public QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {

        if (queryMediaFileRequest == null) {
            queryMediaFileRequest = new QueryMediaFileRequest();
        }

        //查询对象
        MediaFile mediaFile = new MediaFile();

        if (StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())) {
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }

        if (StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())) {
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }


        if (StringUtils.isNotEmpty(queryMediaFileRequest.getTag())) {
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }


        //条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains());

        //example条件

        Example<MediaFile> example = Example.of(mediaFile, exampleMatcher);

        //分页
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;

        if (size <= 0) {
            size = 10;
        }


        //分页查询

        Pageable pageable = new PageRequest(page, size);

        Page<MediaFile> all = mediaFileRepository.findAll(example, pageable);

        //获取总记录数
        long totalElements = all.getTotalElements();
        //获取数据列表
        List<MediaFile> content = all.getContent();

        //组装数据

        QueryResult queryResult = new QueryResult();
        queryResult.setList(content);
        queryResult.setTotal(totalElements);


        QueryResponseResult queryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);


        return queryResponseResult;
    }
}
