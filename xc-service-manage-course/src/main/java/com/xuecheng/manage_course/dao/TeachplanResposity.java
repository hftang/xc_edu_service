package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author hftang
 * @date 2019-03-07 17:04
 * @desc   springDataJPa
 */
@Api(value = "课程根目录查询",description = "根据 课程id parentid 查询 根目录")
public interface TeachplanResposity extends JpaRepository<Teachplan,String> {

    //根据 courseId 和 parenterid
    @ApiOperation("查询根目录")
    public List<Teachplan> findTeachplanByCourseidAndParentid(String courseId,String parentId);
}
