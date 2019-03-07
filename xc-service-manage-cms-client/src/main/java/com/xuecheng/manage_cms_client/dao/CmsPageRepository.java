package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author hftang
 * @date 2019-03-06 16:00
 * @desc
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {

}
