package com.example.demo.dao.demo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.entity.demo.MqMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2023-11-15
 */
@Mapper
public interface MqMessageMapper extends BaseMapper<MqMessage> {

}
