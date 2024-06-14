package com.example.demo.service.elasticsearch;


import com.example.demo.model.elasticsearch.ShipOrderInfo;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.request.ShipOrderInfoRequest;
import org.springframework.stereotype.Service;

@Service
public interface ShipOrderInfoService {

    PageData<ShipOrderInfo> search(ShipOrderInfoRequest request);

    void addBatch() throws Exception;

    boolean deleteShipOrderInfo() ;
}
