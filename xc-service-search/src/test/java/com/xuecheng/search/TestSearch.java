package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

/**
 * @author hftang
 * @date 2019-03-14 18:17
 * @desc
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {


    @Autowired
    RestHighLevelClient client;

    @Autowired
    RestClient restClient;

    //搜索全部记录
    @Test
    public void test_search01() throws IOException {

        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //设置类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //搜索全部
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发").minimumShouldMatch("80%"));
        //source 字段源过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});
        //设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索匹配结果
        SearchHits hits = searchResponse.getHits();
        //搜索总记录数
        Long totalHits = hits.totalHits;

        SearchHit[] hitsHits = hits.getHits();
        for (SearchHit hitsHit : hitsHits) {
            String id = hitsHit.getId();
            Map<String, Object> sourceAsMap = hitsHit.getSourceAsMap();

            Object name = sourceAsMap.get("name");

            System.out.println(name);



        }


    }
}
