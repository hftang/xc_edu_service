package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CoursePublishResult;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author hftang
 * @date 2019-03-07 15:50
 * @desc
 */
@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {
    @Autowired
    CourseService courseService;


    //查询课程计划
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {

        return courseService.findTeachplanList(courseId);
    }

    //添加课程计划
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachPlan(@RequestBody Teachplan teachplan) {



        return courseService.addTeachPlan(teachplan);
    }

    @Override
    @RequestMapping("/course")
    public QueryResponseResult<CourseInfo> findCourseList(int page, int size, CourseListRequest courseListRequest) {


        return null;
    }

    /**
     * 添加课程和课程图片
     * @param courseId
     * @param pic
     * @return
     */

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {

        System.out.println("controller::: courseId:"+courseId+"pic::"+pic);


        return courseService.addCoursePic(courseId,pic);
    }

    /***
     * 查询课程图片
     * @param courseId
     * @return
     */
    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {


        return courseService.findCoursePic(courseId);
    }

    /***
     * 删除课程的图片
     * @param courseId
     * @return
     */
    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult delCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }

    /***
     * 根据课程id 查询课程视图详情
     * @param courseId
     * @return
     */
    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseView(@PathVariable("id") String courseId) {

        return courseService.getCourseView(courseId);
    }

    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable("id") String id) {
        return courseService.preview(id);
    }

    //一键发布
    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish(@PathVariable("id") String id) {


        return courseService.publish(id);
    }


}
