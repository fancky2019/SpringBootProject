package com.example.demo.service.demo.impl;

import com.example.demo.dao.demo.MqMessageMapper;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.service.demo.IMqMessageService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2023-11-15
 */
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements IMqMessageService {

}
