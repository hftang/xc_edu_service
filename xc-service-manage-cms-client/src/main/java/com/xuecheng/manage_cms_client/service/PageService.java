package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author hftang
 * @date 2019-03-06 16:03
 * @desc
 */
@Service
public class PageService {

    private Logger logger= LoggerFactory.getLogger(PageService.class);



    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    CmsSiteRepository cmsSiteRepository;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    GridFsTemplate gridFsTemplate;


    public void savePageToServerPath(String pageId){

        //1 根据pageid 查询 cmspage
        CmsPage cmsPage = findCmsPageByPageId(pageId);

        //2 得到html的id ，从 cmspage 中获取htmlFieldId
        String htmlFileId = cmsPage.getHtmlFileId();



        //3 从 gridfs 中查询 html文件夹

        InputStream stream = getFileById(htmlFileId);

        if(stream==null){
            logger.error(" getFileById  获取的文件流为空 htmlFileId："+htmlFileId);
            return;
        }

        //4 保存到物理服务器上

        //4.1 得到站点的物理路径

        CmsSite cmsSite = findCmsSiteBySiteId(cmsPage.getSiteId());
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();



        //4.2 得到页面的物理路径
        String pagePath=sitePhysicalPath+cmsPage.getPagePhysicalPath()+cmsPage.getPageName();

        logger.info("路径："+pagePath);
         FileOutputStream outputStream = null;
        try {
            String pagepath="D:\\heima29\\save\\"+cmsPage.getPageName()+".html";
            outputStream = new FileOutputStream(new File(pagepath));
            IOUtils.copy(stream, outputStream);
        }catch (Exception e){
            logger.error("保存静态页面失败"+e.toString());
        }finally {
            if(stream!=null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }

    public CmsSite findCmsSiteBySiteId(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
          return   optional.get();
        }

        return null;
    }


    public InputStream getFileById(String htmlFileId){
        //文件对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //操作流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public CmsPage findCmsPageByPageId(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }
}
