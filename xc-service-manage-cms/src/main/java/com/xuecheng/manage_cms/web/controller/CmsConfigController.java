package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.api.cms.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author hftang
 * @date 2019-03-04 17:27
 * @desc
 */

@RestController
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {

    @Autowired
    PageService PageService;


    @Override
    @GetMapping("/getmodel/{id}")
    @ResponseBody
    public CmsConfig getModel(@PathVariable("id") String id) {
        return PageService.getConfigById(id);
    }
}
