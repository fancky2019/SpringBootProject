package com.example.demo.service.demo.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.demo.MqFailLogMapper;
import com.example.demo.model.entity.demo.MqFailLog;
import com.example.demo.service.demo.IMqFailLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2022-11-30
 */
@Service
public class MqFailLogServiceImpl extends ServiceImpl<MqFailLogMapper, MqFailLog> implements IMqFailLogService {

}
