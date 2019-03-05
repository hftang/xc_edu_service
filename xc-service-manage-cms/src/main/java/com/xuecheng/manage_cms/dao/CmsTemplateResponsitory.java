package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author hftang
 * @date 2019-03-05 9:34
 * @desc
 */
public interface CmsTemplateResponsitory extends MongoRepository<CmsTemplate,String> {

}
