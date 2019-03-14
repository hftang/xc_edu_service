package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author hftang
 * @date 2019-03-13 17:18
 * @desc 站点的
 */
public interface CmsSiteResponsitory extends MongoRepository<CmsSite,String> {


}
