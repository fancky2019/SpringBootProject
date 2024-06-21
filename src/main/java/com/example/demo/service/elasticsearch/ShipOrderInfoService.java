package com.example.demo.service.elasticsearch;


import com.example.demo.model.elasticsearch.ShipOrderInfo;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.request.ShipOrderInfoRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public interface ShipOrderInfoService {

    PageData<ShipOrderInfo> search(ShipOrderInfoRequest request) throws Exception;

    void addBatch() throws Exception;

    boolean deleteShipOrderInfo() ;

    void aggregationTopBucketQuery(ShipOrderInfoRequest request) throws JsonProcessingException;

    void aggregationStatisticsQuery(ShipOrderInfoRequest request) throws JsonProcessingException;


}
