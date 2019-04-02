package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author hftang
 * @date 2019-03-29 11:08
 * @desc
 */
public interface CourseMarketRepository extends JpaRepository<CourseMarket, String> {
}