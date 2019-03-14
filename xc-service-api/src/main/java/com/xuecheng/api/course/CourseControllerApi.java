package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CoursePublishResult;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

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

    @ApiOperation("查询我的课程列表")
    public QueryResponseResult<CourseInfo> findCourseList(
            int page,
            int size,
            CourseListRequest courseListRequest
    );
//
//    @ApiOperation("查询分类")
//    public CategoryNode findList();
//
//    @ApiOperation("获取课程基本信息")
//    public CourseBase getCourseBaseById(String courseId) throws RuntimeException;
//
//    @ApiOperation("更新课程基本信息")
//    public ResponseResult updateCourseBase(String id,CourseBase courseBase);
//
//    @ApiOperation("获取课程营销信息")
//    public CourseMarket getCourseMarketById(String courseId);
//    @ApiOperation("更新课程营销信息")
//    public ResponseResult updateCourseMarket(String id,CourseMarket courseMarket);
//
        @ApiOperation("添加课程与课程图片之间的关联")
    public ResponseResult addCoursePic(String courseId,String pic);

        @ApiOperation("查询课程图片")
        public CoursePic findCoursePic(String courseId);
        @ApiOperation("删除课程的图片根据课程id")
        public ResponseResult delCoursePic(String courseId);

        @ApiOperation("课程视图的查询")
        public CourseView courseView(String courseId);

        @ApiOperation("预览课程")
        public CoursePublishResult preview(String id);

        @ApiOperation("课程发布")
        public CoursePublishResult publish(@PathVariable("id") String id);
}
