package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author hftang
 * @date 2019-03-04 17:22
 * @desc
 */
public interface CmsConfigResponsitory extends MongoRepository<CmsConfig,String> {


}
