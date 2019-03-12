package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigResponsitory;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateResponsitory;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author hftang
 * @date 2019-02-27 16:43
 * @desc 定义页面查询方法，根据条件查询暂时不实现：
 */

@Service
public class PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    CmsConfigResponsitory cmsConfigResponsitory;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmsTemplateResponsitory cmsTemplateResponsitory;
    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    public GridFSBucket gridFSBucket;
    @Autowired
    RabbitTemplate rabbitTemplate;


    /***
     * 页面列表分页查询
     * @param page 当前页码
     * @param size 页面显示个数
     * @param queryPageRequest 查询条件
     * @return 页面列表
     */
    public QueryResponseResult<CourseBase> findList(int page, int size, QueryPageRequest queryPageRequest) {

        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }

        if (page < 0) {
            page = 1;
        }
        page = page - 1;//为了适应接口

        if (size <= 0) {
            size = 20;
        }
        //条件匹配器
        //根据名称模糊查询 需要自定义 字符串 模糊查询 规则
        ExampleMatcher exampleMatcher=ExampleMatcher.matching().withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());

        CmsPage cmsPage=new CmsPage();
        //条件值

        //站点id
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }

        //页面别名
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }

        //创建条件实例
        Example<CmsPage> example=Example.of(cmsPage, exampleMatcher);

        //分页对象
        PageRequest pageable = new PageRequest(page, size);
        //分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);

        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());

        return new QueryResponseResult<CourseBase>(CommonCode.SUCCESS, cmsPageQueryResult);
    }

    /***
     * 添加页面
     * @param page
     * @return
     */

    public CmsPageResult addPage(CmsPage page){

        //先检查是否存在了
        CmsPage cmspage = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(page.getPageName(), page.getSiteId(), page.getPageWebPath());

        if(cmspage!=null){
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }

        if(cmspage==null){
            //不存在就添加
            page.setPageId(null);//添加页面的pageid 由springdata 自动生成
            cmsPageRepository.save(page);
            CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, page);
            return cmsPageResult;
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    /**
     * 根据id 查询 cmspage
     */

    public CmsPage findPageByid(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            CmsPage cmsPage = optional.get();

            return cmsPage;
        }

        return null;
    }

    /***
     * 根据 id 更新 cmspage
     * @param id
     * @param cmsPage
     * @return
     */
    public CmsPageResult update(String id,CmsPage cmsPage){

        //1先查询 查询到了才能修改
        CmsPage one = findPageByid(id);
        if(one!=null){
            one.setTemplateId(cmsPage.getTemplateId());
            one.setSiteId(cmsPage.getSiteId());
            one.setPageAliase(cmsPage.getPageAliase());
            one.setPageName(cmsPage.getPageName());
            one.setPageWebPath(cmsPage.getPageWebPath());
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            one.setDataUrl(cmsPage.getDataUrl());
            //执行
            CmsPage save = cmsPageRepository.save(one);
            if(save!=null){
                return new CmsPageResult(CommonCode.SUCCESS,save);
            }
        }
            return new CmsPageResult(CommonCode.FAIL,null);
    }

    /***
     * 根据id 删除页面
     * @param id
     * @return
     */

    public ResponseResult delete(String id){
        //先查询存在不存在
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);

    }

    /**
     * 根据id 获得 cmsconfig
     * @param id
     * @return
     */

    public CmsConfig getConfigById(String id){
        Optional<CmsConfig> optional = cmsConfigResponsitory.findById(id);
        if(optional.isPresent()){
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }

        return null;

    }


    //页面静态化

    public String getPageHtml(String pageId){
        /***
         * 1、获取页面的dataurl
         * 2、远程获取dataurl 数据的模型
         * 3、获取模板信息
         * 4、执行页面静态化
         */

        Map model = getModelByPageId(pageId);
        if (model==null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }

        //获取模板信息
        String template = getTemplateByPageId(pageId);
        if(StringUtils.isEmpty(template)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态
        String html = gengerateHtml(model, template);
        return html;
    }


    //页面发布
    public ResponseResult post(String pageId){

        //1 页面生成静态化页面
        String pageHtml = getPageHtml(pageId);
        //2 将页面存入 gridfs中
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        //3 发送 mq消息
        sendPostPage(pageId);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    /***
     * 向mq发送消息
     * @param pageId
     */
    private void sendPostPage(String pageId) {

        //0 获取到页面的信息 获得名字
        CmsPage cmsPage = findPageByid(pageId);
        if(cmsPage==null){
            ExceptionCast.cast(CmsCode.INVALID_PARAM);
        }

//        拼装消息
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("pageId",pageId);

        String msg = JSON.toJSONString(hashMap);

        //站点
        String siteId = cmsPage.getSiteId();



        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,msg);



    }

    /**
     * 保存静态页面到 gridfs的方法
     * @param pageId
     * @param htmlContent
     * @return
     */
    private CmsPage saveHtml(String pageId,String htmlContent){

        //0 获取到页面的信息 获得名字
        CmsPage cmsPage = findPageByid(pageId);
        if(cmsPage==null){
            ExceptionCast.cast(CmsCode.INVALID_PARAM);
        }


        String pageName = cmsPage.getPageName();



        //1 保存到gridfs中

        InputStream inputStream = null;
        try {
            inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            ObjectId objectId = gridFsTemplate.store(inputStream, pageName);

            //2 更新 cmspage 中的 pageId

            cmsPage.setHtmlFileId(objectId.toString());

            cmsPageRepository.save(cmsPage);

            return cmsPage;

        } catch (IOException e) {
            e.printStackTrace();
        }









        return null;
    }

    /***
     * 执行静态模板
     * @param model
     * @param template
     * @return
     */

    private String gengerateHtml(Map model, String template) {
        //创建配置对象
        Configuration configuration=new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader templateLoader=new StringTemplateLoader();
        templateLoader.putTemplate("template",template);
        //向configuration中配置加载器
        configuration.setTemplateLoader(templateLoader);
        //获取template
        try {
            Template template1 = configuration.getTemplate("template");
            //调用api进行静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return  content;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        return null;

    }

    //获取模板信息
    private String getTemplateByPageId(String pageId) {
        CmsPage cmsPage = findPageByid(pageId);
        if(cmsPage==null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOT_FOUND);
        }
        //获取模板的id
        String templateId = cmsPage.getTemplateId();

        if(StringUtils.isEmpty(templateId)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        //查询模板信息

        Optional<CmsTemplate> optional = cmsTemplateResponsitory.findById(templateId);
        if(optional.isPresent()){
            CmsTemplate cmsTemplate = optional.get();
            //获取模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //从 GridFs中获取
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开一个下载流对象
            GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource对象 获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, downloadStream);
            //从流中获取数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;


    }

    //获取model
    private Map getModelByPageId(String pageId) {
        //获取cmspage页
        CmsPage cmsPage = findPageByid(pageId);
        if(cmsPage==null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOT_FOUND);
        }
        //获取dataurl
        String dataUrl = cmsPage.getDataUrl();
        //如果为空判断
        if(StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }

        //使用 resttemplate 获取 dataurl数据

        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();

        return body;
    }




}
