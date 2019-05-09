package com.example.demo.controller.rabc;

import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.service.rabc.MybatisAnnotationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("rabc/mybatisAnnotation")
public class MybatisAnnotationController {
    private static Logger logger = LogManager.getLogger(MybatisAnnotationController.class);
    @Resource
    private MybatisAnnotationService mybatisAnnotationService;

    @PostMapping("/insert")
    public MessageResult<Void> insert(@RequestBody Users user) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            messageResult = mybatisAnnotationService.insert(user);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    @PostMapping("/batchInsert")
    public MessageResult<Void> batchInsert(@RequestBody List<Users> list) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            messageResult= mybatisAnnotationService.batchInsert(list);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    @GetMapping("/selectById")
    public MessageResult<Users> selectById(Integer id) {
        MessageResult<Users> messageResult = new MessageResult<>();
        try {
            messageResult = mybatisAnnotationService.selectById(id);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    @PostMapping("/batchDelete")
    public MessageResult<Void> batchDelete(@RequestBody List<Integer> list) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            messageResult= mybatisAnnotationService.batchDelete(list);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }

    @GetMapping("/selectAll")
    public MessageResult<List<Users>> selectAll() {
        MessageResult<List<Users>> messageResult = new MessageResult<>();
        try {
            messageResult = mybatisAnnotationService.selectAll();
        } catch (Exception ex) {
            messageResult.setSuccess(false);
            messageResult.setMessage(ex.getMessage());
            logger.error(ex.toString());
        } finally {
            return messageResult;
        }
    }
}
