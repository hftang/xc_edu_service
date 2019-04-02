package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author hftang
 * @date 2019-03-27 13:48
 * @desc
 */
public interface TeachplanMediaPubRepository extends JpaRepository<TeachplanMediaPub, String> {



    //根据课程id来删除记录
//    long deleteByCourseId(String courseId);

    long deleteByCourseId(String courseId);


}
