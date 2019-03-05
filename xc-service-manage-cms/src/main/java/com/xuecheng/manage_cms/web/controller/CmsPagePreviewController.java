package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * @author hftang
 * @date 2019-03-05 10:27
 * @desc 页面预览
 */

@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    private PageService pageService;

    @RequestMapping(value = "/cms/preview/{pageId}",method = RequestMethod.GET)
    public void preview(@PathVariable("pageId")String pageId) throws IOException {
        String html = pageService.getPageHtml(pageId);

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(html.getBytes("utf-8"));



    }
}
