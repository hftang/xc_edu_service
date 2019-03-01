package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author hftang
 * @date 2019-02-27 11:33
 * @desc 创建Dao，继承MongoRepository，并指定实体类型和主键类型。
 */
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {

    //根据页面名称查询
//    CmsPage findCmsPageName(String pageName);
    CmsPage findCmsPageByPageName(String pageName);

    //根据页面名称和类型查询
    CmsPage findByPageNameAndPageType(String pageName, String pageType);

    //根据站点和页面类型查询记录数
    int countBySiteIdAndPageType(String siteId, String pageType);

    //根据站点和页面类型分页查询
    Page<CmsPage> findBySiteIdAndPageType(String siteId, String pageType, Pageable pageable);

    //添加页面 根据页面名称 站点id 页面访问路径查询

    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String pageWebPath);



}
