package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author hftang
 * @date 2019-03-28 16:30
 * @desc
 */
public interface SysDictionaryRepository  extends MongoRepository<SysDictionary, String> {

    //根据字典分类查询字典信息
    SysDictionary findByDType(String dType);
}