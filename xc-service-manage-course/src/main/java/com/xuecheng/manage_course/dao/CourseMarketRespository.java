package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author hftang
 * @date 2019-03-12 16:49
 * @desc 课程的营销信息
 */
public interface CourseMarketRespository  extends JpaRepository<CourseMarket,String> {

}
