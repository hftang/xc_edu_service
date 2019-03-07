package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author hftang
 * @date 2019-03-07 13:50
 * @desc 课程api
 */

@Api(value = "课程管理接口" , description = "课程管理接口 提供课程的 增删改查")
public interface CourseControllerApi {

    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseId);
    @ApiOperation("添加课程计划")
   public ResponseResult addTeachPlan(Teachplan teachplan);
}
