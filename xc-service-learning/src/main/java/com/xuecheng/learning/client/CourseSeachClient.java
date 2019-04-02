package com.xuecheng.learning.client;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author hftang
 * @date 2019-04-02 17:32
 * @desc
 */

@FeignClient(value = "XC-SERVICE-SEARCH")
public interface CourseSeachClient {

    //课程计划的id 来查询课程的媒资

    @GetMapping("/search/course/getmedia/{teachplanId}")
    public TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId);


}
