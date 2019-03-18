package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author hftang
 * @date 2019-03-14 16:29
 * @desc 测试索引
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    RestClient restClient;

    //测试创建索引库
    @Test
    public void testCreateIndex() throws IOException {

        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");

        //设置参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards", "1").put("number_of_replicas", "0"));

        //指定映射
        createIndexRequest.mapping("doc", "{\n" +
                "\t\"properties\":{\n" +
                "\t\t\"name\":{\n" +
                "\t\t\t\"type\":\"text\"\n" +
                "\t\t},\n" +
                "\t\t\"discription\":{\n" +
                "\t\t\t\"type\":\"text\"\n" +
                "\t\t},\n" +
                "\t\t\"studymodel\":{\n" +
                "\t\t\t\"type\":\"keyword\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}", XContentType.JSON);

        //创建索引的客户端
        IndicesClient indices = restHighLevelClient.indices();

        //创建索引库

        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);

        //获取结果
        boolean acknowledged = createIndexResponse.isAcknowledged();

        System.out.println(acknowledged);

    }

    //测试删除索引库
    @Test
    public void testDelIndex() throws IOException {

        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");

        IndicesClient indices = restHighLevelClient.indices();

        DeleteIndexResponse delete = indices.delete(deleteIndexRequest);

        boolean acknowledged = delete.isAcknowledged();

        System.out.println(acknowledged);
    }

    //添加文档到索引库

    @Test
    public void testAddDoc() throws Exception {

        //准备json 数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud 实战");
        jsonMap.put("description", "微服务架构入门 eureka注册中心 springcloud");
        jsonMap.put("studymodel", "2001");

        //请求对象
        IndexRequest indexRequest = new IndexRequest("xc_course", "doc");
        //指定索引文档内容
        indexRequest.source(jsonMap);
        //索引响应对象
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest);
        //获取响应结果
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println(result);

    }

    //查询文档
    @Test
    public void testQuery() throws IOException {

        //查询的请求对象
        GetRequest getRequest = new GetRequest("xc_course", "doc", "ZoSOe2kBd_ob8Ulx3WDo");

        GetResponse documentFields = restHighLevelClient.get(getRequest);
        //得到文档的内容

        Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();

        System.out.println(sourceAsMap);


    }

    //根据id查询
    @Test
    public void testByIdsQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] ids = new String[]{"1", "2"};
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
        searchSourceBuilder.fetchSource(new String[]{"name"},new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest);
        SearchHits hits = search.getHits();

        long totalHits = hits.getTotalHits();
        SearchHit[] hits1 = hits.getHits();

        for (SearchHit hit : hits1) {

            String id = hit.getId();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            Iterator<Map.Entry<String, Object>> iterator = sourceAsMap.entrySet().iterator();

            while (iterator.hasNext()){
                Map.Entry<String, Object> next = iterator.next();
                String key = next.getKey();
                Object value = next.getValue();

                System.out.println(value);
            }


            System.out.println(hit);
        }


    }

    //matchquery匹配
    @Test
    public void testMatchQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //搜素方式 matchquery
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架").minimumShouldMatch("70%"));

        //设置源字段的过滤
        searchSourceBuilder.fetchSource(new String[]{"name"},new String[]{});

        //向请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        //执行搜索 向es 执行http请求

        SearchResponse search = restHighLevelClient.search(searchRequest);

        SearchHits hits = search.getHits();

        long totalHits = hits.getTotalHits();

        SearchHit[] hits1 = hits.getHits();
        for (SearchHit documentFields : hits1) {
            String id = documentFields.getId();
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            Iterator<Map.Entry<String, Object>> iterator = sourceAsMap.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String, Object> next = iterator.next();
                Object value = next.getValue();
                Object value1 = next.getValue();

                System.out.println(value);

            }


        }



    }

    //multimatchquery
    @Test
    public void testMultiMatchQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.multiMatchQuery("spring css", "name", "description").minimumShouldMatch("80%").field("name",10));
        sourceBuilder.fetchSource(new String[]{"name","description"},new String[]{});
        searchRequest.source(sourceBuilder);
        SearchResponse searchRespon = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchRespon.getHits();
        SearchHit[] hits1 = hits.getHits();

        for (SearchHit fields : hits1) {
            Map<String, Object> sourceAsMap = fields.getSourceAsMap();
            Iterator<Map.Entry<String, Object>> iterator = sourceAsMap.entrySet().iterator();

            while(iterator.hasNext()){
                Map.Entry<String, Object> next = iterator.next();
                Object value = next.getValue();

                System.out.println(value);

            }


        }
    }

    //boolean

    @Test
    public void testBooleanQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //mulitmatchquery
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description").minimumShouldMatch("80%").field("name", 10);
        //termquery
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("studymodel", "201001");

        //定义booleanquery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //同时满足2条
        boolQueryBuilder.must(matchQueryBuilder);
        boolQueryBuilder.must(termsQueryBuilder);
        //查询
        sourceBuilder.query(boolQueryBuilder);
        //设置要显示的过滤字段
        sourceBuilder.fetchSource(new String[]{"name","description"},new String[]{});
        searchRequest.source(sourceBuilder);
        SearchResponse searchRespon = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchRespon.getHits();
        SearchHit[] hits1 = hits.getHits();

        for (SearchHit fields : hits1) {
            Map<String, Object> sourceAsMap = fields.getSourceAsMap();
            Iterator<Map.Entry<String, Object>> iterator = sourceAsMap.entrySet().iterator();

            while(iterator.hasNext()){
                Map.Entry<String, Object> next = iterator.next();
                Object value = next.getValue();

                System.out.println(value);

            }


        }
    }

    //实现过滤器

        @Test
        public void testFilter() throws IOException {
            SearchRequest searchRequest = new SearchRequest("xc_course");
            searchRequest.types("doc");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            //mulitmatchquery
            MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description").minimumShouldMatch("80%").field("name", 10);



            //定义booleanquery
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //定义一个过滤器
            boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
            //定义价格区间 大于30 小于40
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(30).lte(40));

            //同时满足2条
            boolQueryBuilder.must(matchQueryBuilder);
            //查询
            sourceBuilder.query(boolQueryBuilder);
            //设置要显示的过滤字段
            sourceBuilder.fetchSource(new String[]{"name","description"},new String[]{});
            searchRequest.source(sourceBuilder);
            SearchResponse searchRespon = restHighLevelClient.search(searchRequest);

            SearchHits hits = searchRespon.getHits();
            SearchHit[] hits1 = hits.getHits();

            for (SearchHit fields : hits1) {
                Map<String, Object> sourceAsMap = fields.getSourceAsMap();
                Iterator<Map.Entry<String, Object>> iterator = sourceAsMap.entrySet().iterator();

                while(iterator.hasNext()){
                    Map.Entry<String, Object> next = iterator.next();
                    Object value = next.getValue();
                    String key = next.getKey();

                    System.out.println(value);

                }


            }
        }

    //实现排序 sort

    @Test
    public void testSort() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //mulitmatchquery
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description").minimumShouldMatch("80%").field("name", 10);



        //定义booleanquery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //定义价格区间 大于30 小于40
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));

        //同时满足2条
        boolQueryBuilder.must(matchQueryBuilder);

        //添加排序
        sourceBuilder.sort("studymodel", SortOrder.DESC);//降序
        sourceBuilder.sort("price",SortOrder.ASC);//价格升序

        //查询
        sourceBuilder.query(boolQueryBuilder);
        //设置要显示的过滤字段
        sourceBuilder.fetchSource(new String[]{"name","description"},new String[]{});
        //设置查询请求源
        searchRequest.source(sourceBuilder);

        SearchResponse searchRespon = restHighLevelClient.search(searchRequest);

        SearchHits hits = searchRespon.getHits();
        SearchHit[] hits1 = hits.getHits();

        for (SearchHit fields : hits1) {
            Map<String, Object> sourceAsMap = fields.getSourceAsMap();
            Iterator<Map.Entry<String, Object>> iterator = sourceAsMap.entrySet().iterator();

            while(iterator.hasNext()){
                Map.Entry<String, Object> next = iterator.next();
                Object value = next.getValue();
                String key = next.getKey();

                System.out.println(value);

            }


        }
    }

    //实现高亮 HeightLight


    @Test
    public void testHeightLight() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //mulitmatchquery
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("spring", "name", "description")
                .minimumShouldMatch("80%").field("name", 10);



        //定义booleanquery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //定义价格区间 大于30 小于40
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));

        //查询
        sourceBuilder.query(boolQueryBuilder);
        //设置要显示的过滤字段
        sourceBuilder.fetchSource(new String[]{"name","description"},new String[]{});
        //设置查询请求源
        searchRequest.source(sourceBuilder);


        //同时满足2条
        boolQueryBuilder.must(matchQueryBuilder);

        //添加排序
        sourceBuilder.sort("studymodel", SortOrder.DESC);//降序
        sourceBuilder.sort("price",SortOrder.ASC);//价格升序

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");

        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));

        sourceBuilder.highlighter(highlightBuilder);

        SearchResponse searchRespon = restHighLevelClient.search(searchRequest);




        SearchHits hits = searchRespon.getHits();
        SearchHit[] hits1 = hits.getHits();

        for (SearchHit fields : hits1) {
            Map<String, Object> sourceAsMap = fields.getSourceAsMap();
            Object name = sourceAsMap.get("name");
            Object description = sourceAsMap.get("description");

            Map<String, HighlightField> highlightFields = fields.getHighlightFields();

            if(highlightFields!=null){
                HighlightField name1 = highlightFields.get("name");

                if(name1!=null){
                    Text[] fragments = name1.getFragments();
                    for (Text fragment : fragments) {
                        System.out.println(fragment);

                    }

                    System.out.println(fragments);
                }
            }



            System.out.println(name);


//            Iterator<Map.Entry<String, Object>> iterator = sourceAsMap.entrySet().iterator();
//
//            while(iterator.hasNext()){
//                Map.Entry<String, Object> next = iterator.next();
//                Object value = next.getValue();
//                String key = next.getKey();
//
//                 System.out.println(value);
//
//            }


        }
    }



}
