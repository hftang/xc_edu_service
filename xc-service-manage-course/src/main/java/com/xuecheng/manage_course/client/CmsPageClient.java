package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author hftang
 * @date 2019-03-12 14:10
 * @desc
 */

@FeignClient(value = "XC-SERVICE-MANAGE-CMS")  //要指定远程调用的服务名称
@Component
public interface CmsPageClient {

    @GetMapping("/cms/page/get/{id}")
    public CmsPage findPageById(@PathVariable("id") String id);
}
