package com.example.demo.service.elasticsearch;

import com.example.demo.elasticsearch.DemoProductRepository;
import com.example.demo.model.elasticsearch.DemoProduct;
import org.apache.poi.ss.formula.functions.T;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;
import org.springframework.stereotype.Service;

//import javax.xml.ws.ServiceMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ESDemoProductService {


    @Autowired
    private DemoProductRepository demoProductRepository;


//    @Autowired
//    private RestHighLevelClient restHighLevelClient;

    /**
     * ElasticsearchRestTemplate 内部封装了
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
     */
    public List<DemoProduct> search(String keyword, Integer pageNum, Integer pageSize) {

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


        term	完全匹配	查询条件必须都是text分词中的，且不能多余，多个分词时必须连续，顺序不能颠倒。	否
        match	完全匹配	match分词结果和text的分词结果有相同的即可，不考虑顺序	是
        match_phrase	完全匹配	match_phrase的分词结果必须在text字段分词中都包含，而且顺序必须相同，而且必须都是连续的。	是
        query_string	完全匹配	query_string中的分词结果至少有一个在text字段的分词结果中，不考虑顺序	是









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

        //多个字段or
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                //查询条件:es支持分词查询，最小是一个词，要精确匹配分词
                //两个字段中or
//                .withQuery(QueryBuilders.boolQuery()
//                        .must(QueryBuilders.queryStringQuery("中国徐家汇").defaultField("product_name"))
//                        .must(QueryBuilders.queryStringQuery("上海中国").defaultField("produce_address"))
//                        .must(QueryBuilders.rangeQuery("price").from("5").to("9"))
//                )

                //在指定字段中查找值
//                .withQuery(QueryBuilders.queryStringQuery("合肥").field("product_name").field("produce_address"))
                .withQuery(QueryBuilders.multiMatchQuery("安徽合肥","product_name","produce_address"))



//                .withQuery(QueryBuilders.rangeQuery("price").from("5").to("9"))//多个条件and 的关系
                //分页
                .withPageable(PageRequest.of(0, 5))
                //排序
                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                //高亮字段显示
//                .withHighlightFields(new HighlightBuilder.Field("product_name"))
                .build();
        SearchHits<DemoProduct> search = elasticsearchRestTemplate.search(nativeSearchQuery, DemoProduct.class);
        List<DemoProduct> products1 = search.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());


        return null;
    }

}
