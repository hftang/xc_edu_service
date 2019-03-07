package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author hftang
 * @date 2019-03-07 15:17
 * @desc
 */

@Mapper
@Repository
public interface TeachplanMapper {

    //课程计划查询
    TeachplanNode selectList(String courseId);
}
