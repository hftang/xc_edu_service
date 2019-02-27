package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author hftang
 * @date 2019-02-27 11:35
 * @desc dao的测试类
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;


    @Test
    public void testFindPage() {

//        int page=0;
//        int size=10;
//        Pageable pageable = PageRequest.of(page,size);


        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);


    }

    @Test
    public void testFindPageNum() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);


    }

    @Test
    public void testInsert() {

        CmsPage cmsPage = new CmsPage();

        cmsPage.setSiteId("s01");

        cmsPage.setTemplateId("t01");

        cmsPage.setPageName("测试页面");

        cmsPage.setPageCreateTime(new Date());

        List<CmsPageParam> cmsPageParams = new ArrayList<>();

        CmsPageParam cmsPageParam = new CmsPageParam();

        cmsPageParam.setPageParamName("param1");

        cmsPageParam.setPageParamValue("value1");

        cmsPageParams.add(cmsPageParam);

        cmsPage.setPageParams(cmsPageParams);

        CmsPage save = cmsPageRepository.save(cmsPage);

        System.out.println(save);
    }

    @Test
    public void testDel() {

        try {
            cmsPageRepository.deleteById("5c7643fe78237144646e19ff");
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    @Test
    public void testUpdate() {

        Optional<CmsPage> optional = cmsPageRepository.findById("5c76460478237160ccaf9e81");
        if (optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            cmsPage.setPageName("hello update");

            cmsPageRepository.save(cmsPage);
        }
    }

}