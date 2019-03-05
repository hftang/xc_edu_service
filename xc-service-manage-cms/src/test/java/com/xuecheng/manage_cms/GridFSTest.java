package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author hftang
 * @date 2019-03-05 14:19
 * @desc
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFSTest {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    /***
     * 测试存文件
     */


    @Test
    public void testStore() throws FileNotFoundException {

        File file = new File("D:\\heima29\\index_banner.ftl");
        FileInputStream inputStream=new FileInputStream(file);

        ObjectId objectId = gridFsTemplate.store(inputStream, "index_banner.ftl");
        System.out.println(objectId);
        //5c7e17047823716e48f501a8

    }

    /**
     * 测试读文件
     */
    @Test
    public void  testQueryFile() throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5c7e17047823716e48f501a8")));
        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, downloadStream);
        String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(s);


    }
}
