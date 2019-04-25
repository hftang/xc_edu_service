package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.*;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author hftang
 * @date 2019-03-07 15:44
 * @desc 课程管理的service
 */
@Service
public class CourseService {

    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanResposity teachplanResposity;
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CoursePicRepository coursePicRepository;

    @Autowired
    CourseMarketRespository courseMarketRespository;

    @Autowired
    CmsPageClient cmsPageClient;
    @Autowired
    CoursePubRepository coursePubRepository;
    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    CourseMapper courseMapper;
    @Autowired
    CourseMarketRepository courseMarketRepository;

    @Autowired
    TeachplanMediaPubRepository teachplanMediaPubRepository;


    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

    //课程计划的查询

    public TeachplanNode findTeachplanList(String courseId) {
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);

        if (teachplanNode == null) {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CDETAILERROR);
        }

        return teachplanNode;

    }

    //添加课程计划
    @Transactional
    public ResponseResult addTeachPlan(Teachplan teachplan) {

        if (teachplan == null || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CmsCode.INVALID_PARAM);
        }

        //课程id
        String courseid = teachplan.getCourseid();

        //parentId
        String parentid = teachplan.getParentid();

        if (StringUtils.isEmpty(parentid)) {
            //创建 根节点id
            parentid = getTeachplanRoot(courseid);
        }

        Optional<Teachplan> optional = teachplanResposity.findById(parentid);
        Teachplan teachplanParent = null;
        if (optional.isPresent()) {
            teachplanParent = optional.get();
        }

        Teachplan newTeachplan = new Teachplan();

        BeanUtils.copyProperties(teachplan, newTeachplan);

        newTeachplan.setParentid(parentid);
        newTeachplan.setCourseid(courseid);
        //设置grade 层级 根据父节点来设置
        String grade = teachplanParent.getGrade();

        if ("1".equals(grade)) {
            newTeachplan.setGrade("2");
        }
        if ("2".equals(grade)) {
            newTeachplan.setGrade("3");
        }

        //保存新节点
        teachplanResposity.save(newTeachplan);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //如果根节点没有查到 就自己创建一个根节点
    private String getTeachplanRoot(String courseId) {

        CourseBase courseBase = null;
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        courseBase = optional.get();

        List<Teachplan> rootList = teachplanResposity.findTeachplanByCourseidAndParentid(courseId, "0");
        if (rootList == null || rootList.size() <= 0) {


            //没有根节点 没查到 要自己创建
            Teachplan teachplan = new Teachplan();

            teachplan.setGrade("1");
            teachplan.setParentid("0");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");

            teachplanResposity.save(teachplan);

            return teachplan.getId();

        }

        return rootList.get(0).getId();//获取到根节点的id
    }

    //添加课程与课程图片
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        CoursePic coursePic = null;
        //先查询
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if (optional.isPresent()) {
            coursePic = optional.get();

            coursePic.setCourseid(courseId);
            coursePic.setPic(pic);
        } else {
            coursePic = new CoursePic();
            coursePic.setCourseid(courseId);
            coursePic.setPic(pic);

        }
        coursePicRepository.save(coursePic);
        System.out.println("service:::" + coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //查询课程图片
    public CoursePic findCoursePic(String courseId) {

        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if (optional.isPresent()) {
            CoursePic coursePic = optional.get();

            return coursePic;
        }

        return null;


    }

    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {

        long num = coursePicRepository.deleteByCourseid(courseId);

        if (num > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        } else {
            return new ResponseResult(CommonCode.FAIL);
        }


    }

    //查询课程视图
    public CourseView getCourseView(String courseId) {

        CourseView courseView = new CourseView();

        //课程的基本信息
        Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(courseId);
        if (optionalCourseBase.isPresent()) {
            courseView.setCourseBase(optionalCourseBase.get());
        }

        //查询课程的图片
        Optional<CoursePic> optionalCoursePic = coursePicRepository.findById(courseId);
        if (optionalCoursePic.isPresent()) {
            courseView.setCoursePic(optionalCoursePic.get());
        }

        //课程的营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRespository.findById(courseId);
        if (courseMarketOptional.isPresent()) {
            courseView.setCourseMarket(courseMarketOptional.get());
        }

        //课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        courseView.setTeachplanNode(teachplanNode);


        return courseView;
    }

    private CourseBase findCourseBaseById(String id) {

        Optional<CourseBase> baseOptional = courseBaseRepository.findById(id);
        if (baseOptional.isPresent()) {
            CourseBase courseBase = baseOptional.get();

            return courseBase;
        }

        ExceptionCast.cast(CourseCode.COURSE_PUBLISH_PERVIEWISNULL);

        return null;
    }

    //预览页面
    public CoursePublishResult preview(String id) {
        CourseBase one = this.findCourseBaseById(id);

        CmsPage cmsPage = new CmsPage();

        cmsPage.setSiteId(publish_siteId);
        cmsPage.setTemplateId(publish_templateId);
        cmsPage.setPageName(id + ".html");
        cmsPage.setPageAliase(one.getName());
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setDataUrl(publish_dataUrlPre + id);

        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        System.out.println(cmsPageResult);

        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        String pageId = cmsPageResult.getCmsPage().getPageId();

        String pageUrl = previewUrl + pageId;

        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);


    }

    //课程发布
    @Transactional
    public CoursePublishResult publish(String id) {
        CourseBase one = this.findCourseBaseById(id);
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageId(id);
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setTemplateId(publish_templateId);
        cmsPage.setPageName(id + ".html");
        cmsPage.setPageAliase(one.getName());
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setDataUrl(publish_dataUrlPre + id);
        //调用cms的一键发布接口将课程详情页面发布到服务器上
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);

        if (!cmsPostPageResult.isSuccess()) {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //保存课程的发布 状态为已发布
        CourseBase courseBase = saveCoursePubState(id);
        if (courseBase == null) {
            ExceptionCast.cast(CommonCode.FAIL);
        }

        //保存课程的索引信息

        //  创建coursePub

        CoursePub coursePub = createCoursePub(id);

        //  保存coursePub到表里
        CoursePub coursePub1 = saveCoursePub(id, coursePub);
        System.out.println(coursePub1);
        //缓存课程信息
        String pageUrl = cmsPostPageResult.getPageUrl();
        //向teachplanMeaiaPub中保存课程媒资信息

        saveTeachplanMediaPub(id);


        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);

    }

    //向teachplanMeaiaPub中保存课程媒资信息
    public void saveTeachplanMediaPub(String courseId) {
        try {

            //从teachplanmedia中查询
            List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);
            //先删除
            teachplanMediaPubRepository.deleteByCourseId(courseId);


            List<TeachplanMediaPub> teachplanMediaPubs = new ArrayList<>();

            for (TeachplanMedia teachplanMedia : teachplanMediaList) {
                TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();

                BeanUtils.copyProperties(teachplanMedia, teachplanMediaPub);
                //设置时间戳
                teachplanMediaPub.setTimestamp(new Date());

                teachplanMediaPubs.add(teachplanMediaPub);
            }
            //将上面list 插入到teachplanMeadiaPub表中
            teachplanMediaPubRepository.saveAll(teachplanMediaPubs);

        } catch (Exception e) {
            String s = e.toString();
            System.out.println(s);
            e.printStackTrace();
        }


    }


    //创建coursePub
    public CoursePub createCoursePub(String courseId) {
        CoursePub coursePub = new CoursePub();
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if (courseBaseOptional.isPresent()) {
            CourseBase courseBase = courseBaseOptional.get();
            //copy到coursePub中
            BeanUtils.copyProperties(courseBase, coursePub);
        }

        //将课程计划信息查到 然后转成json串 给 coursePub
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        String teachPlan = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(teachPlan);

        return coursePub;
    }

    //保存coursePub
    private CoursePub saveCoursePub(String coursId, CoursePub coursePub) {
        Optional<CoursePub> repository = coursePubRepository.findById(coursId);

        CoursePub coursePub_new = null;
        //先查 有则更新 无则插入
        if (repository.isPresent()) {
            coursePub_new = repository.get();
        } else {
            coursePub_new = new CoursePub();
        }

        BeanUtils.copyProperties(coursePub, coursePub_new);
        coursePub_new.setCharge("203002");
        coursePub_new.setValid("204001");
        coursePub_new.setId(coursId);

        //时间戳 给logstach使用
        coursePub_new.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String format_date = simpleDateFormat.format(new Date());
        coursePub_new.setPubTime(format_date);
        //保存
        coursePubRepository.save(coursePub_new);

        return coursePub_new;


    }

    //更改课程的状态 已发布的状态为202002
    private CourseBase saveCoursePubState(String courseId) {
        CourseBase courseBase = this.findCourseBaseById(courseId);
        courseBase.setStatus("202002");
        courseBaseRepository.save(courseBase);
        return courseBase;
    }

    //保存课程与媒资之间的关系
    public ResponseResult save(TeachplanMedia teachplanMedia) {

        if (teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //课程计划是否是3级
        String teachplanId = teachplanMedia.getTeachplanId();
        Optional<Teachplan> optional = teachplanResposity.findById(teachplanId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //查询到了教学计划
        Teachplan teachplan = optional.get();

        String grade = teachplan.getGrade();

        if (StringUtils.isEmpty(grade) || !grade.equals("3")) {
            ExceptionCast.cast(CourseCode.COURSE_MEDIS_TEACHPLAN_GRADEERROR);
        }

        Optional<TeachplanMedia> optional1 = teachplanMediaRepository.findById(teachplanId);
        TeachplanMedia one;
        if (optional1.isPresent()) {
            one = optional1.get();
        } else {
            one = new TeachplanMedia();
        }
        //保存到数据库
        one.setCourseId(teachplan.getCourseid());
        one.setMediaId(teachplanMedia.getMediaId());
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        one.setMediaUrl(teachplanMedia.getMediaUrl());
        one.setTeachplanId(teachplanId);

        teachplanMediaRepository.save(one);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //查询分类信息
    public CategoryNode findCategoryList() {

        CategoryNode categoryNode = categoryMapper.selectList();

        return categoryNode;
    }

    public AddCourseResult addCourseBase(CourseBase courseBase) {
        // TODO 需要根据moongodb的数据字典查, 暂时设死
        courseBase.setStatus("202001");
        CourseBase save = courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS, save.getId());
    }

    //查询课程
    public QueryResponseResult<CourseInfo> findCourseList(String companyId, int page, int size, CourseListRequest courseListRequest) {

        //不能让查询条件为空
        courseListRequest = courseListRequest == null ? new CourseListRequest() : courseListRequest;

        //企业id,将companyId传给dao
        courseListRequest.setCompanyId(companyId);

        page = page <= 0 ? 1 : page;
        size = size <= 0 ? 20 : size;
        //设置分页参数
        PageHelper.startPage(page, size);
        //分页查询 组装数据
        Page<CourseInfo> courseInfoPage = courseMapper.findCourseListPage(courseListRequest);

        List<CourseInfo> courseInfoList = courseInfoPage.getResult();
        long total = courseInfoPage.getTotal();//总记录数
        //查询结果集
        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setList(courseInfoList);
        queryResult.setTotal(total);
        return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS, queryResult);
    }

    //获取课程基本信息
    public CourseBase getCourseBaseById(String courseId) {
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if (!courseBaseOptional.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_NOT_FOUND);
        }
        return courseBaseOptional.get();
    }

    //更新课程
    public ResponseResult updateCourseBase(String id, CourseBase courseBase) {
        CourseBase one = getCourseBaseById(id);
        one.setName(courseBase.getName());
        one.setUsers(courseBase.getUsers());
        one.setMt(courseBase.getMt());
        one.setSt(courseBase.getSt());
        one.setGrade(courseBase.getGrade());
        one.setStudymodel(courseBase.getStudymodel());
        one.setDescription(courseBase.getDescription());
        courseBaseRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(courseId);
        if (!courseMarketOptional.isPresent()) {
            return null;
        }
        return courseMarketOptional.get();
    }

    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket one = getCourseMarketById(id);
        if (one == null) {
            courseMarket.setId(id);
            courseMarketRepository.save(courseMarket);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        one.setCharge(courseMarket.getCharge());
        one.setValid(courseMarket.getValid());
        one.setQq(courseMarket.getQq());
        one.setPrice(courseMarket.getPrice());
        one.setStartTime(courseMarket.getStartTime());
        one.setEndTime(courseMarket.getEndTime());
        courseMarketRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
