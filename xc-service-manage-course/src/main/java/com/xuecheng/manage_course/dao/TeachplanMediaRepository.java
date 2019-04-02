package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachplanMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author hftang
 * @date 2019-03-27 13:48
 * @desc
 */
public interface TeachplanMediaRepository extends JpaRepository<TeachplanMedia,String> {

    //根据课程id来查询列表
    List<TeachplanMedia> findByCourseId(String courserId);



}
