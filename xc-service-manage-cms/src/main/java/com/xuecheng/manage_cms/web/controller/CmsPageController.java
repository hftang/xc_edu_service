package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hftang
 * @date 2019-02-27 10:57
 * @desc
 */
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    PageService pageService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {

//
//        ResultCode resultCode= CommonCode.SUCCESS;
//        QueryResult queryList=new QueryResult();
//        queryList.setTotal(2);
//
//        List list=new ArrayList();
//        CmsPage cmsPage01=new CmsPage();
//        cmsPage01.setPageName("测试页面001");
//        list.add(cmsPage01);
//
//        CmsPage cmsPage02=new CmsPage();
//        cmsPage02.setPageName("测试页面002");
//        list.add(cmsPage02);
//
//        queryList.setList(list);
//
//        QueryResponseResult queryResponseResult = new QueryResponseResult(resultCode, queryList);
//
//
//        return queryResponseResult;
        return pageService.findList(page, size, queryPageRequest);


    }
}