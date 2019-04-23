package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hftang
 * @date 2019-04-22 17:41
 * @desc
 */
@Mapper
@Repository
public interface XcMeunMapper {
    //根据用户的id查询用户的权限

    public List<XcMenu> selectPermissonByUserId(String userId);
}
