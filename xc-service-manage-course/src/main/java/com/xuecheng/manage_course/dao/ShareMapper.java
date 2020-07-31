package com.xuecheng.manage_course.dao;

import com.xuecheng.manage_course.entity.Share;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author hftang
 * @date 2020-07-30 13:48
 * @desc
 */
@Repository
@Mapper
public interface ShareMapper {

    int insertShareBean(Share share);

}
