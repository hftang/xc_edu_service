package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public QueryResponseResult<CourseBase> findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {

        return pageService.findList(page, size, queryPageRequest);


    }

    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {

        return pageService.addPage(cmsPage);
    }

    @Override
    @GetMapping("/get/{id}")
    public CmsPage findPageById(@PathVariable("id") String id) {

        return pageService.findPageByid(id);
    }

    /**
     * 修改 对应的 cmspage
     * @param id
     * @param cmsPage
     * @return
     */
    @Override
    @PutMapping("/edit/{id}")
    public CmsPageResult edit(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return pageService.update(id,cmsPage);
    }

    @Override
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {

        return pageService.delete(id);
    }

    @Override
    @PostMapping("/postPage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {



        return pageService.post(pageId);
    }

    //保存页面

    @Override
    @PostMapping("/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        CmsPageResult save = pageService.save(cmsPage);

        return save;
    }

    //课程发布
    @Override
    @PostMapping("/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage) {

        return pageService.postPageQuick(cmsPage);
    }
}
