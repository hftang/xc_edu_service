package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.learning.client.CourseSeachClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hftang
 * @date 2019-04-02 17:28
 * @desc
 */
@Service
public class LearningService {

    @Autowired
    CourseSeachClient courseSeachClient;


    //获取课程学习地址（视频播放地址）
    public GetMediaResult getmedia(String courseId, String teachplanId) {

        //检验学生的权限


        //远程调用
        TeachplanMediaPub mediaPub = courseSeachClient.getmedia(teachplanId);
        if (mediaPub == null || StringUtils.isEmpty(mediaPub.getMediaUrl())) {
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }
        return new GetMediaResult(CommonCode.SUCCESS,mediaPub.getMediaUrl());

    }
}
