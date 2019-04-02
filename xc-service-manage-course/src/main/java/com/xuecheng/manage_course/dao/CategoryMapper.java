package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hftang
 * @date 2019-03-28 15:49
 * @desc
 */
@Mapper
public interface CategoryMapper {
    public CategoryNode selectList();
}
