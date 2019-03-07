package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author hftang
 * @date 2019-03-06 16:02
 * @desc
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {


}
