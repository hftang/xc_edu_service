package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.TeachplanMapper;
import com.xuecheng.manage_course.dao.TeachplanResposity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author hftang
 * @date 2019-03-07 15:44
 * @desc 课程管理的service
 */
@Service
public class CourseService {

    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanResposity teachplanResposity;
    @Autowired
    CourseBaseRepository courseBaseRepository;

    //课程计划的查询

    public TeachplanNode findTeachplanList(String courseId){
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);

        if(teachplanNode==null){
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CDETAILERROR);
        }

        return teachplanNode;

    }

    //添加课程计划
    @Transactional
    public ResponseResult addTeachPlan(Teachplan teachplan) {

        if(teachplan==null|| StringUtils.isEmpty(teachplan.getCourseid())||StringUtils.isEmpty(teachplan.getPname()))
        {
        ExceptionCast.cast(CmsCode.INVALID_PARAM);
        }

        //课程id
        String courseid = teachplan.getCourseid();

        //parentId
        String parentid = teachplan.getParentid();

        if (StringUtils.isEmpty(parentid)) {
            //创建 根节点id
            parentid = getTeachplanRoot(courseid);
        }

        Optional<Teachplan> optional = teachplanResposity.findById(parentid);
        Teachplan teachplanParent=null;
        if(optional.isPresent()){
             teachplanParent = optional.get();
        }

        Teachplan newTeachplan = new Teachplan();

        BeanUtils.copyProperties(teachplan,newTeachplan);

        newTeachplan.setParentid(parentid);
        newTeachplan.setCourseid(courseid);
        //设置grade 层级 根据父节点来设置
        String grade = teachplanParent.getGrade();

        if("1".equals(grade)){
            newTeachplan.setGrade("2");
        }
        if("2".equals(grade)){
            newTeachplan.setGrade("3");
        }

        //保存新节点
        teachplanResposity.save(newTeachplan);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //如果根节点没有查到 就自己创建一个根节点
    private String getTeachplanRoot(String courseId){

        CourseBase courseBase = null;
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()){
           return null;
        }
        courseBase = optional.get();

        List<Teachplan> rootList = teachplanResposity.findTeachplanByCourseidAndParentid(courseId, "0");
        if(rootList==null|| rootList.size()<=0){



            //没有根节点 没查到 要自己创建
            Teachplan teachplan = new Teachplan();

            teachplan.setGrade("1");
            teachplan.setParentid("0");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");

            teachplanResposity.save(teachplan);

        return teachplan.getId();

        }

        return rootList.get(0).getId();//获取到根节点的id
    }
}
