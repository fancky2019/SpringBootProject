package com.example.demo.service.demo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.entity.demo.DemoOrderItem;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2023-11-30
 */
public interface IDemoOrderItemService extends IService<DemoOrderItem> {
    int batchInsertSession();
}
