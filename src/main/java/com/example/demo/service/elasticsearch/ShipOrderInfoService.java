package com.example.demo.service.elasticsearch;


import com.example.demo.model.elasticsearch.ShipOrderInfo;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.request.ShipOrderInfoRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Service
public interface ShipOrderInfoService {

    PageData<ShipOrderInfo> search(ShipOrderInfoRequest request) throws Exception;

    void addBatch() throws Exception;

    boolean deleteShipOrderInfo();

    void aggregationTopBucketQuery(ShipOrderInfoRequest request) throws JsonProcessingException;

    LinkedHashMap<String, BigDecimal> aggregationStatisticsQuery(ShipOrderInfoRequest request) throws Exception;

    LinkedHashMap<Object, Double> dateHistogramStatisticsQuery(ShipOrderInfoRequest request) throws JsonProcessingException;


}
