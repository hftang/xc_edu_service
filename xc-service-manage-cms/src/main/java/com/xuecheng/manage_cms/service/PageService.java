package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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


    /***
     * 页面列表分页查询
     * @param page 当前页码
     * @param size 页面显示个数
     * @param queryPageRequest 查询条件
     * @return 页面列表
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {

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

        return new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
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

}
