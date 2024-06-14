package com.example.demo.service.elasticsearch;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.elasticsearch.DemoProductRepository;
import com.example.demo.model.elasticsearch.DemoProduct;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.request.DemoProductRequest;
import com.example.demo.model.pojo.Page;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.PointInTimeBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

//import javax.xml.ws.ServiceMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ESDemoProductService {


    @Autowired
    private DemoProductRepository demoProductRepository;


//    @Autowired
//    private RestHighLevelClient restHighLevelClient;

    /**
     * ElasticsearchRestTemplate 内部封装了RestHighLevelClient
     */
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;



    /*
     区间查询：
     //闭区间查询
    QueryBuilders.rangeQuery("fieldName").from("fieldValue1").to("fieldValue2");
    //开区间查询，默认是true，也就是包含
    QueryBuilders.rangeQuery("fieldName").from("fieldValue1").to("fieldValue2").includeUpper(false).includeLower(false);
    //大于
    QueryBuilders.rangeQuery("fieldName").gt("fieldValue");
    //大于等于
    QueryBuilders.rangeQuery("fieldName").gte("fieldValue");
    //小于
    QueryBuilders.rangeQuery("fieldName").lt("fieldValue");
    //小于等于
    QueryBuilders.rangeQuery("fieldName").lte("fieldValue");
     */


    /*
    多个查询条件拼接
    QueryBuilders.boolQuery()
    QueryBuilders.boolQuery().must();//文档必须完全匹配条件，相当于and
    QueryBuilders.boolQuery().mustNot();//文档必须不匹配条件，相当于not
    QueryBuilders.boolQuery().should();//至少满足一个条件，这个文档就符合should，相当于or
     */


    /*
    matchPhraseQuery和matchQuery等的区别，在使用matchQuery等时，在执行查询时，搜索的词会被分词器分词，而使用matchPhraseQuery时，
    不会被分词器分词， 而是直接以一个短语的形式查询，而如果你在创建索引所使用的field的value中没有这么一个短语（顺序无差，且连接在一起），
    那么将查询不出任何结果。


    不再建议使用scroll API进行深度分页。如果要分页检索超过 Top 10,000+ 结果时，推荐使用：PIT + search_after。
    Search After 不支持跳页功能
     */
    public PageData<DemoProduct> search(DemoProductRequest request) {

        //region backup
        //        public class SimpleElasticsearchRepository<T, ID> implements ElasticsearchRepository<T, ID>

        /*
            public Page<T> searchSimilar(T entity, @Nullable String[] fields, Pageable pageable) {
        Assert.notNull(entity, "Cannot search similar records for 'null'.");
        Assert.notNull(pageable, "'pageable' cannot be 'null'");
        MoreLikeThisQuery query = new MoreLikeThisQuery();
        query.setId(this.stringIdRepresentation(this.extractIdFromBean(entity)));
        query.setPageable(pageable);
        if (fields != null) {
            query.addFields(fields);
        }

        SearchHits<T> searchHits = (SearchHits)this.execute((operations) -> {
            return operations.search(query, this.entityClass, this.getIndexCoordinates());
        });
        SearchPage<T> searchPage = SearchHitSupport.searchPageFor(searchHits, pageable);
        return (Page)SearchHitSupport.unwrapSearchHits(searchPage);
    }
         */


//        DemoProduct demoProduct = new DemoProduct();
//        demoProduct.setProductName("df");
//        demoProduct.setId(1);
//        String[] fields = new String[]{"product_name"};
//        Pageable pageable = PageRequest.of(pageNum, pageSize);
//        Page<DemoProduct> products = demoProductRepository.searchSimilar(demoProduct, fields, pageable);


        /*
         match_phrase是分词的，text也是分词的。match_phrase的分词结果必须在text字段分词中都包含，而且顺序必须相同，而且必须都是连续的。
        query_string 支持分词 查询text类型的字段。和match_phrase区别的是，query_string查询text类型字段，不需要连续，顺序还可以调换。

        matchQuery	支持分词    结果分词查询
        termQuery	不支持分词  结果完全匹配


       keyword字段不分词

        term	完全匹配	查询条件必须都是text分词中的，且不能多余，多个分词时必须连续，顺序不能颠倒。	否
        match	完全匹配	match分词结果和text的分词结果有相同的即可，不考虑顺序	是
        match_phrase	完全匹配	match_phrase的分词结果必须在text字段分词中“都包含”，而且顺序必须相同，而且必须都是连续的。	是
        query_string	完全匹配	query_string中的分词结果至少有一个在text字段的分词结果中，不考虑顺序	是


//暂时理解 query_string包含match的功能。


//        QueryBuilders.termQuery()
//        QueryBuilders.matchQuery()
//        QueryBuilders.matchPhraseQuery()
//        QueryBuilders.queryStringQuery()

like查询：利用wildcard通配符查询实现，其中？和*分别代替一个和多个字符。




         */


        //多个字段and
//        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
//                //查询条件:es支持分词查询，最小是一个词，要精确匹配分词
//                .withQuery(QueryBuilders.queryStringQuery("china fancky").defaultField("product_name"))
//                .withQuery(QueryBuilders.rangeQuery("price").from("5").to("9"))//多个条件and 的关系
//                //分页
//                .withPageable(PageRequest.of(0, 5))
//                //排序
//                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
//                //高亮字段显示
////                .withHighlightFields(new HighlightBuilder.Field("product_name"))
//                .build();
//        SearchHits<DemoProduct> search = elasticsearchRestTemplate.search(nativeSearchQuery, DemoProduct.class);
//        List<DemoProduct> products1=search.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());


//        //查询title字段中，包含 ”开发”、“开放" 这个字符串的document；相当于把"浦东开发开放"分词了，再查询；
//        QueryBuilders.queryStringQuery("开发开放").defaultField("title");
//        //不指定feild，查询范围为所有feild
//        QueryBuilders.queryStringQuery("青春");
//       //指定多个feild
//        QueryBuilders.queryStringQuery("青春").field("title").field("content");
//
//
//        QueryBuilders.termQuery("title", "开发开放");
//        QueryBuilders.termsQuery("fieldName", "fieldlValue1","fieldlValue2...");
//
//        QueryBuilders.matchQuery("title", "开发开放");
//        QueryBuilders.multiMatchQuery("fieldlValue", "fieldName1", "fieldName2", "fieldName3");
//
//
//

        //正常使用match query 进行简单模糊就可以，query_string 功能复杂。

//        指定分词
//        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("wenti", wenti).analyzer("ik_max_word"));
        //多个字段or
//        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
//                //查询条件:es支持分词查询，最小是一个词，要精确匹配分词
//                //两个字段中or
////                .withQuery(QueryBuilders.boolQuery()
////                        .must(QueryBuilders.queryStringQuery("中国徐家汇").defaultField("product_name"))
////                        .must(QueryBuilders.queryStringQuery("上海中国").defaultField("produce_address"))
////                        .must(QueryBuilders.rangeQuery("price").from("5").to("9"))
////                )
//
//                //在指定字段中查找值
////                .withQuery(QueryBuilders.queryStringQuery("合肥").field("product_name").field("produce_address"))
//                // .withQuery(QueryBuilders.multiMatchQuery("安徽合肥", "product_name", "produce_address"))
//
////
//                //模糊查询待测试 : Wildcard 性能会比较慢。如果非必要，尽量避免在开头加通配符 ? 或者 *，这样会明显降低查询性能
//
//                .withQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("product_name", "产品名称1570018"))
//
//                .must(QueryBuilders.wildcardQuery("product_style", "*" + "productstyle" + "*")))//必须要加keyword，否则查不出来
//                //SEARCH_AFTER 不用指定 from size
////                .withQuery(QueryBuilders.rangeQuery("price").from("5").to("9"))//多个条件and 的关系
//                //分页
//                .withPageable(PageRequest.of(900000, 5))
//                //排序
//                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
//                //高亮字段显示
////                .withHighlightFields(new HighlightBuilder.Field("product_name"))
//                .withTrackTotalHits(true)//解除最大1W条限制
//                .build();

        //endregion

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (request.getId() != null && request.getId() > 0) {
            boolQueryBuilder.must(QueryBuilders.termQuery("id", request.getId()));
        }
        if (StringUtils.isNotEmpty(request.getGuid())) {
            //guid 设置keyword  不成功 ES8
//            boolQueryBuilder.must(QueryBuilders.termQuery("guid.keyword", request.getGuid()));
            //es7
            boolQueryBuilder.must(QueryBuilders.termQuery("guid", request.getGuid()));
        }
        if (StringUtils.isNotEmpty(request.getProductName())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("product_name", request.getProductName()));
        }
        if (StringUtils.isNotEmpty(request.getProductStyle())) {
            //ES8要转消息，ES7不用转小写
//            //模糊查询待测试 : Wildcard 性能会比较慢。如果非必要，尽量避免在开头加通配符 ? 或者 *，这样会明显降低查询性能
//            boolQueryBuilder.must(QueryBuilders.wildcardQuery("product_style", "*" + request.getProductStyle().toLowerCase() + "*"));

            //模糊查询待测试 : Wildcard 性能会比较慢。如果非必要，尽量避免在开头加通配符 ? 或者 *，这样会明显降低查询性能
            boolQueryBuilder.must(QueryBuilders.wildcardQuery("product_style", "*" + request.getProductStyle() + "*"));

        }
        if (request.getCreateTimeStart() != null) {
            boolQueryBuilder.must(QueryBuilders.
                    rangeQuery("create_time")
                    .gte(request.getCreateTimeStart()).lte(request.getCreateTimeEnd()));
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                //查询条件:es支持分词查询，最小是一个词，要精确匹配分词
                //在指定字段中查找值
//                .withQuery(QueryBuilders.queryStringQuery("合肥").field("product_name").field("produce_address"))
                // .withQuery(QueryBuilders.multiMatchQuery("安徽合肥", "product_name", "produce_address"))

                .withQuery(boolQueryBuilder)//必须要加keyword，否则查不出来
                //SEARCH_AFTER 不用指定 from size
//                .withQuery(QueryBuilders.rangeQuery("price").from("5").to("9"))//多个条件and 的关系
                //分页：page 从0开始
                .withPageable(PageRequest.of(request.getPageIndex(), request.getPageSize()))
                //排序
                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                //高亮字段显示
//                .withHighlightFields(new HighlightBuilder.Field("product_name"))
                .withTrackTotalHits(true)//解除最大1W条限制
                .build();
//        nativeSearchQuery.setTrackTotalHitsUpTo(10000000);
        SearchHits<DemoProduct> search = elasticsearchRestTemplate.search(nativeSearchQuery, DemoProduct.class);
        List<DemoProduct> productList = search.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());

        long count = search.getTotalHits();
        PageData<DemoProduct> pageData = new PageData<>();
        pageData.setCount(count);
        pageData.setData(productList);
//        elasticsearchRestTemplate.bulkUpdate();
//        elasticsearchRestTemplate.bulkIndex();
//        elasticsearchRestTemplate.delete()
        return pageData;
    }


    /*
     searchAfter
    上一页解决：
    对于某一页，正序search_after该页的最后一条数据id为下一页，
    则逆序search_after该页的第一条数据id则为上一页
     */

    /**
     * searchAfter
     * searchAfter 不支持跳页，类似app 一页一页请求
     * @param request
     * @return
     */
    public PageData<DemoProduct> searchAfter(DemoProductRequest request) {


        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (request.getId() != null && request.getId() > 0) {
            boolQueryBuilder.must(QueryBuilders.termQuery("id", request.getId()));
        }
        if (StringUtils.isNotEmpty(request.getGuid())) {
            //guid 设置keyword  不成功 ES8
//            boolQueryBuilder.must(QueryBuilders.termQuery("guid.keyword", request.getGuid()));
            //es7
            boolQueryBuilder.must(QueryBuilders.termQuery("guid", request.getGuid()));
        }
        if (StringUtils.isNotEmpty(request.getProductName())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("product_name", request.getProductName()));
        }
        if (StringUtils.isNotEmpty(request.getProductStyle())) {
            //ES8要转消息，ES7不用转小写
//            //模糊查询待测试 : Wildcard 性能会比较慢。如果非必要，尽量避免在开头加通配符 ? 或者 *，这样会明显降低查询性能
//            boolQueryBuilder.must(QueryBuilders.wildcardQuery("product_style", "*" + request.getProductStyle().toLowerCase() + "*"));

            //模糊查询待测试 : Wildcard 性能会比较慢。如果非必要，尽量避免在开头加通配符 ? 或者 *，这样会明显降低查询性能
            boolQueryBuilder.must(QueryBuilders.wildcardQuery("product_style", "*" + request.getProductStyle() + "*"));

        }
        if (request.getCreateTimeStart() != null) {
            boolQueryBuilder.must(QueryBuilders.
                    rangeQuery("create_time")
                    .gte(request.getCreateTimeStart()).lte(request.getCreateTimeEnd()));
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                //查询条件:es支持分词查询，最小是一个词，要精确匹配分词
                //在指定字段中查找值
//                .withQuery(QueryBuilders.queryStringQuery("合肥").field("product_name").field("produce_address"))
                // .withQuery(QueryBuilders.multiMatchQuery("安徽合肥", "product_name", "produce_address"))

                .withQuery(boolQueryBuilder)//必须要加keyword，否则查不出来
                //SEARCH_AFTER 不用指定 from size
//                .withQuery(QueryBuilders.rangeQuery("price").from("5").to("9"))//多个条件and 的关系
                //分页：page 从0开始
                .withPageable(PageRequest.of(request.getPageIndex(), request.getPageSize()))
                //排序
                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                //高亮字段显示
//                .withHighlightFields(new HighlightBuilder.Field("product_name"))
                .withTrackTotalHits(true)//解除最大1W条限制
                .build();
        //前段传上次查询的排序最后的id
        if (request.getSearchAfterId() > 0) {
            // "search_after": [124648691, "624812"],
            //searchAfter 是sort 排序的字段值  最后一条的值
            List<Object> searchAfterList = new ArrayList<>();
            searchAfterList.add(request.getSearchAfterId());
            nativeSearchQuery.setSearchAfter(searchAfterList);
        }

//        nativeSearchQuery.setTrackTotalHitsUpTo(10000000);
        SearchHits<DemoProduct> search = elasticsearchRestTemplate.search(nativeSearchQuery, DemoProduct.class);
        List<DemoProduct> productList = search.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());

        long count = search.getTotalHits();
        PageData<DemoProduct> pageData = new PageData<>();
        pageData.setCount(count);
        pageData.setData(productList);
//        elasticsearchRestTemplate.bulkUpdate();
//        elasticsearchRestTemplate.bulkIndex();
//        elasticsearchRestTemplate.delete()
        return pageData;
    }



    //region searchAfter+PIT

    //这种多个PIT,使用不带PIT的

/*   PIT (时间点)
   PIT的本质：存储索引数据状态的轻量级视图。

pit 视图：取时间点的快照视图数据，防止期间发生变更造成数据差异
pit 会过期

    */


//    /**
//     * 分页查询 search_after + SIZE + PIT 查询
//     * @param indices 索引名用于创建PIT
//     * @param sortNum 排序值
//     * @param pageSize 页数
//     * @return
//     * @throws Exception
//     */
//    public Integer findByPageableBySearchAfterPIT(String indices,int sortNum,int pageSize) throws Exception {
//
//        // 1. 创建时间点，过期时间5分钟
//        String pitId = createPit(indices,5);
//        // 2.结合 search after 和 PIT ID 进行深度分页
//        final PointInTimeBuilder pitBuilder = new PointInTimeBuilder(pitId);
//
//        // 3.创建搜索条件
//        final SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource()
//                .pointInTimeBuilder(pitBuilder) // 指定 pit
//                .from(0)
//                .size(pageSize)
//                .searchAfter(new Object[]{sortNum})
//                .sort("id", SortOrder.ASC);
//        SearchRequest searchRequest = new SearchRequest();//indices 无需指定索引名
//        searchRequest.source(searchSourceBuilder);
//        //4. 获取结果
//        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//        Object[] arrays = new Object[1];
//        System.out.println(search.getHits().getHits());
//        for(SearchHit hit : search.getHits().getHits()){
//            Map<String, Object> map = hit.getSourceAsMap();
//            System.out.println(JSONObject.toJSONString(map));
//            System.out.println(hit.getSortValues()[0]);
//            arrays = hit.getSortValues();
//        }
//        System.out.println("sort值" + arrays[0]);
//
//        // 最后关闭 Point In Time
//        final ClosePointInTimeRequest closePointInTimeRequest = new ClosePointInTimeRequest(pitId);
//        restHighLevelClient.closePointInTime(closePointInTimeRequest,RequestOptions.DEFAULT);
//
//        if(arrays[0] == null) return null;
//        else return Integer.parseInt(arrays[0].toString());
//    }
//
//    /**
//     * 创建 PIT OpenPointInTimeRequest支持版本：highlevelclient7.16以上
//     * @param indices 索引名
//     * @param keep_alive 存活时间 单位：分钟
//     * @throws Exception
//     */
//    private String createPit(String indices,int keep_alive) throws Exception{
//        // 构造 pit open Request
//        //1. 根据索引创建时间点
//        final OpenPointInTimeRequest pitRequest = new OpenPointInTimeRequest(indices);
//        //2. 设置存活时间
//        pitRequest.keepAlive(TimeValue.timeValueMinutes(keep_alive));
//
//        //打开 pit 获取 pitId
//        final OpenPointInTimeResponse pitResponse = restHighLevelClient.openPointInTime(pitRequest, RequestOptions.DEFAULT);
//        //3. 读取返回的时间点 id
//        final String pitId = pitResponse.getPointInTimeId();
//
//        return pitId;
//    }
//endregion

}
