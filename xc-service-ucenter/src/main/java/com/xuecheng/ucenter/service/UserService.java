package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hftang
 * @date 2019-04-17 11:14
 * @desc
 */

@Service
public class UserService {

    @Autowired
    XcUserRepository xcUserRepository;

    @Autowired
    XcCompanyUserRepository xcCompanyUserRepository;

    //根据账号查询用户信息

    public XcUserExt getUserExt(String username) {
        //根据账号来查询用户信息
        XcUser xcUser = this.findXcUserByUsername(username);

        if (xcUser == null) {
            return null;
        }
        String userId = xcUser.getId();

        //根据用户的id查询用户所属的公司id
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(userId);
        //获取到用户的公司id
        String companyId = null;
        if (xcCompanyUser != null) {
            companyId = xcCompanyUser.getCompanyId();
        }

        XcUserExt xcUserExt = new XcUserExt();

        BeanUtils.copyProperties(xcUser, xcUserExt);

        xcUserExt.setCompanyId(companyId);
        return xcUserExt;
    }

    public XcUser findXcUserByUsername(String username) {
        return xcUserRepository.findByUsername(username);
    }


}
