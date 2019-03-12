package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author hftang
 * @date 2019-03-11 14:38
 * @desc
 */
public interface FileSystemRepository extends MongoRepository<FileSystem,String> {
}
