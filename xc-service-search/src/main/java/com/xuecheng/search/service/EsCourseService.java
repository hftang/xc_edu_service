package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hftang
 * @date 2019-03-18 14:20
 * @desc
 */
@Service
public class EsCourseService {

    @Value("${xuecheng.course.index}")
    private String index;

    @Value("${xuecheng.course.type}")
    private String type;

    @Value("${xuecheng.course.source_field}")
    private String source_field;

    @Autowired
    private RestHighLevelClient client;



    //课程搜素 根据关键字进行查询
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam)  {
        if(courseSearchParam==null){
            courseSearchParam=new CourseSearchParam();
        }
        //创建搜索对象
        SearchRequest searchRequest = new SearchRequest(index);
        //type
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();

        String[] source_field_array = source_field.split(",");
        //过滤源字段 一个参数是要显示那些字段 第二个参数是 不显示那些字段
        searchSourceBuilder.fetchSource(source_field_array,new String[]{});

        //boolean查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();


        //根据关键字来搜索
        if(StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "description", "teachplan")
                    .minimumShouldMatch("70%")
                    .field("name", 10);

            //boolean是组装这些查询
            boolQueryBuilder.must(multiMatchQueryBuilder);

        }

        //过滤 按难度来划分
        if(StringUtils.isNotEmpty(courseSearchParam.getMt())){
            //根据分类 一级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }

        if(StringUtils.isNotEmpty(courseSearchParam.getSt())){
            //根据分类 二级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }

        //根据难度等级
        if(StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));

        searchSourceBuilder.highlighter(highlightBuilder);



        //设置Boolean查询对象 到  querybuild中
        searchSourceBuilder.query(boolQueryBuilder);

        //设置分页查询

        if(page<=0){
            page=1;
        }
        if(size<=0){
            size=12;
        }
        int from=(page-1)*size;
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //
        searchRequest.source(searchSourceBuilder);



        QueryResult<CoursePub> queryResult=new QueryResult<>();
        List<CoursePub> list=new ArrayList<>();

        try {
            SearchResponse searchResponse = client.search(searchRequest);

            //获取响应结果
            SearchHits hits = searchResponse.getHits();
            //总条数
            long totalHits = hits.getTotalHits();

            queryResult.setTotal(totalHits);

            SearchHit[] searchHits = hits.getHits();
            for (SearchHit fields : searchHits) {
                CoursePub coursePub = new CoursePub();



                //原文档
                Map<String, Object> sourceAsMap = fields.getSourceAsMap();

                //取出id
                String id = (String) sourceAsMap.get("id");
                coursePub.setId(id);

                String name = (String) sourceAsMap.get("name");
                //取出高亮字段
                Map<String, HighlightField> highlightFields = fields.getHighlightFields();
                if(highlightFields!=null){
                    HighlightField highlightFieldName = highlightFields.get("name");
                    if(highlightFieldName!=null){
                        //获取段信息
                        Text[] nameFragments = highlightFieldName.getFragments();
                        StringBuffer stringBuffer = new StringBuffer();
                        for (Text nameFragment : nameFragments) {
                            stringBuffer.append(nameFragment);
                        }
                        name = stringBuffer.toString();
                    }
                }


                coursePub.setName(name);
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                Double price = (Double) sourceAsMap.get("price");
                if(price!=null){
                    coursePub.setPrice(price);
                }

                Double price_old = (Double) sourceAsMap.get("price_old");
                if(price_old!=null){
                    coursePub.setPrice_old(price_old);
                }

                list.add(coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryResult.setList(list);

        QueryResponseResult<CoursePub> pubQueryResponseResult = new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,queryResult);
        return pubQueryResponseResult;
    }
}
