package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hftang
 * @date 2019-03-12 14:16
 * @desc
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFeign {

    @Autowired
    CmsPageClient cmsPageClient;

    @Test
    public void test_feign(){
        CmsPage cmsPage = cmsPageClient.findPageById("5a754adf6abb500ad05688d9");

        System.out.println(cmsPage);


    }
}
