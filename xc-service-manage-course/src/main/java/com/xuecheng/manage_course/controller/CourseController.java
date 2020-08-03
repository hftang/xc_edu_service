package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CoursePublishResult;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hftang
 * @date 2019-03-07 15:50
 * @desc
 */
@RestController
@RequestMapping("/course")
public class CourseController extends BaseController implements CourseControllerApi {
    @Autowired
    CourseService courseService;


    //查询课程计划 根据课程id查询课程计划
    //添加方法授权
    @PreAuthorize("hasAuthority('course_teachplan_list')")
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {

        return courseService.findTeachplanList(courseId);
    }

    //添加课程计划
    //添加方法授权
    @PreAuthorize("hasAuthority('course_teachplan_add')")
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
     *
     * @param courseId
     * @param pic
     * @return
     */
    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        System.out.println("controller::: courseId:" + courseId + "pic::" + pic);
        return courseService.addCoursePic(courseId, pic);
    }

    /***
     * 查询课程图片
     * @param courseId
     * @return
     */
    //添加方法授权
    //当用户有了 这个权限course_pic_list 才能查询图片列表
    @PreAuthorize("hasAuthority('course_pic_list')")
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

    // //保存课程与媒资计划关联
    @Override
    @PostMapping("/savemedia")
    public ResponseResult saveMedia(@RequestBody TeachplanMedia teachplanMedia) {


        return courseService.save(teachplanMedia);
    }

    /**
     * 添加课程基础信息
     *
     * @param courseBase
     * @return
     */
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {

        return courseService.addCourseBase(courseBase);
    }

    /**
     * 查询我的课程列表
     *
     * @param page
     * @param size
     * @param courseListRequest
     * @return
     */
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(
            @PathVariable("page") int page,
            @PathVariable("size") int size,
            CourseListRequest courseListRequest,
            HttpServletRequest request) {
        //调用工具类取出用户信息
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        if (userJwt == null) {
            ExceptionCast.cast(CommonCode.UNAUTHENTICATED);
        }
        String company_id = userJwt.getCompanyId();

        //当前用户所属单位的id
//        String company_id = "1";

        QueryResponseResult<CourseInfo> courseList = courseService.findCourseList(company_id, page, size, courseListRequest);

        return courseList;
    }

    /**
     * 获取课程基础信息
     *
     * @param courseId
     * @return
     */
//    @PreAuthorize("hasAuthority('course_get_baseinfo')")
    @GetMapping("/getCoursebaseById/{courseId}")
    public CourseBase getCourseBaseById(@PathVariable("courseId") String courseId) {
        return courseService.getCourseBaseById(courseId);
    }

    /**
     * 更新课程基础信息
     *
     * @param id
     * @param courseBase
     * @return
     */
    @PutMapping("/updateCoursebase/{id}")
    public ResponseResult updateCourseBase(@PathVariable("id") String id, @RequestBody CourseBase courseBase) {
        String s = courseBase.toString();
        System.out.println(s);

        return courseService.updateCourseBase(id, courseBase);
    }

    /**
     * 获取课程营销信息
     *
     * @param courseId
     * @return
     */
    @GetMapping("/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    /**
     * 更新课程营销信息
     *
     * @param id
     * @param courseMarket
     * @return
     */
    @PostMapping("/coursemarket/update/{id}")
    public ResponseResult updateCourseMarket(@PathVariable("id") String id, @RequestBody CourseMarket courseMarket) {
        return courseService.updateCourseMarket(id, courseMarket);
    }


}
