package com.example.demo.service.camunda;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessInstanceWithVariablesImpl;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CamundaService {
    @Resource
    private ProcessEngine processEngine;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private IdentityService identityService;

    @Autowired
    public CamundaService() {

    }

    @PostConstruct
    public void init() {
        String flag = "1";// tSysConfigService.getSysConfigValueByConfigCode("INIT_PROCESS_FLAG");
        String configIp = "1";//tSysConfigService.getSysConfigValueByConfigCode("ip_address");
        if ("1".equals(flag)) {
            try {
                String ip = InetAddress.getLocalHost().getHostAddress();
                if (!configIp.equals(ip)) {
                    //选定一台服务器做初始化的流程部署
                    return;
                }
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                DeploymentBuilder builder = repositoryService.createDeployment();
                builder.name("init_process");
                org.springframework.core.io.Resource[] resources = resolver.getResources("classpath:bpmn/*.bpmn");
                for (org.springframework.core.io.Resource resource : resources) {
                    builder.addClasspathResource("bpmn/".concat(resource.getFilename()));
                }
                builder.deploy();
                log.info("init_process success");
            } catch (Exception e) {
                log.error("init_process fail", e);
            }
        }
    }

    public Deployment deploy(String deploymentName, String bpmnFileName) {

        Deployment deployment = repositoryService.createDeployment()
                .name(deploymentName)
                .addClasspathResource(bpmnFileName)
                .deploy();
        return deployment;
    }

    public String startProcess(String processDefinitionKey, String businessKey, Map<String, Object> processVariables, String initiator) {
        //  log.debug("start process instance, processDefinitionKey: {}, businessKey={}, processVariables: {}", processName, businessKey, processVariables);
        //   Map processVariablesMap = CamundaTools.convertProcessVariablesFromEntity(processVariables);

        identityService.setAuthenticatedUserId(initiator);//设置流程发起人
        ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, processVariables);
        log.debug("start process instance success, processInstanceId: {}", processInstance.getId());
        String taskId = null;
        String processInstanceId = null;
        if (processInstance != null) {
            //act_ru_task 表会保存活跃的流程：根据proc_inst_id获取任务id_(taskId)
            //任务id会随着审核节点变动，流程proc_inst_id不会变
            //当任务完成数据会从act_ru_task表删除，可在act_hi_procinst查询流程
            taskId = ((ProcessInstanceWithVariablesImpl) processInstance).getExecutionEntity().getTasks().get(0).getId();
            processInstanceId = processInstance.getId();
        }
        return processInstanceId;
    }

    public String completedTask(String taskId, Map<String, Object> processVariables) throws Exception {
        //  log.debug("start process instance, processDefinitionKey: {}, businessKey={}, processVariables: {}", processName, businessKey, processVariables);
        //   Map processVariablesMap = CamundaTools.convertProcessVariablesFromEntity(processVariables);
        String processInstanceId = getProcessInstanceIdByTaskId(taskId);
        taskService.complete(taskId, processVariables);

        String nextTaskId = getActiveByProcessInstanceId(processInstanceId);
        if (StringUtils.isEmpty(nextTaskId)) {
            //流程走完
            return "completed";
        }
        Task nextTask = getTaskByTaskId(nextTaskId);
        // 任务认领
        //  claimTask(nextTaskId, "fancky");

        setTaskGroup(nextTaskId, "10000");
        Task task = taskService.createTaskQuery().taskId(nextTaskId).singleResult();
        return "";
    }

    public void setTaskGroup(String taskId, String groupCode) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            taskService.addCandidateGroup(taskId, groupCode);
        } else {
            throw new Exception("未查询到对应的task信息！");
        }
    }

    public String claimTask(String taskId, String assignee) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            if (StringUtils.isBlank(task.getAssignee())) {
                taskService.claim(task.getId(), assignee);
                return "";
            } else {
                throw new Exception("任务已被他人领取!");
            }
        } else {
            throw new Exception("未查询到任务!");
        }
    }

    public Task getTaskByTaskId(String taskId) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new Exception("未查询到对应的task信息！");
        }
        return task;
    }

    public String getProcessInstanceIdByTaskId(String taskId) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new Exception("未查询到对应的task信息！");
        }
        String processInstanceId = task.getProcessInstanceId();
        return processInstanceId;
    }

    /**
     * 根据流程实例 ID 获取当前活动任务
     * 多实例节点不适用
     *
     * @param processInstanceId
     * @return
     */
    public String getActiveByProcessInstanceId(String processInstanceId) {
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
        String taskId = null;
        if (task != null) {
            taskId = task.getId();
        }
        return taskId;
    }

    /**
     * 根据流程实例 ID 获取当前活动任务（支持多实例节点）
     *
     * @param processInstanceId
     * @return
     */
    public List<Task> getActiveTaskByProcessInstanceId(String processInstanceId) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        return taskList;
    }
}
