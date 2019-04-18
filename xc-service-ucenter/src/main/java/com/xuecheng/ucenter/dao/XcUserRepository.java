package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author hftang
 * @date 2019-04-17 11:01
 * @desc
 */
public interface XcUserRepository extends JpaRepository<XcUser, String> {

    //根据用户的id 查询该用户所属的公司id
    XcUser findByUsername(String userId);
}
