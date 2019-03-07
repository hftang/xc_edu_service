package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hftang
 * @date 2019-03-06 16:49
 * @desc 监听 mq
 */
@Component
public class ConsumerPostPage {

    Logger logger= LoggerFactory.getLogger(ConsumerPostPage.class);

    @Autowired
    PageService pageService;




   @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg){

        Map map = JSON.parseObject(msg, Map.class);

        String pageId = (String) map.get("pageId");
        //校验
        CmsPage cmsPage = pageService.findCmsPageByPageId(pageId);
        if(cmsPage==null){
            logger.error("校验失败 pageId："+pageId);
            return;
        }

        pageService.savePageToServerPath(pageId);



    }


}
