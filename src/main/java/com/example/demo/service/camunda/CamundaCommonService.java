package com.example.demo.service.camunda;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.FormProperty;
import org.camunda.bpm.engine.form.StartFormData;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.impl.RepositoryServiceImpl;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.el.Expression;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.javax.el.ExpressionFactory;
import org.camunda.bpm.engine.impl.javax.el.ValueExpression;
import org.camunda.bpm.engine.impl.juel.ExpressionFactoryImpl;
import org.camunda.bpm.engine.impl.juel.SimpleContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.PvmActivity;
import org.camunda.bpm.engine.impl.pvm.PvmTransition;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.runtime.*;
import org.camunda.bpm.engine.task.Comment;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

@Service("workFlowService")
public class CamundaCommonService {

    private final Logger log = LoggerFactory.getLogger(CamundaCommonService.class);

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

//    @Autowired
//    private TSysConfigService tSysConfigService;

    @PostConstruct
    public void init() {
        String flag ="1";// tSysConfigService.getSysConfigValueByConfigCode("INIT_PROCESS_FLAG");
        String configIp = "1";//tSysConfigService.getSysConfigValueByConfigCode("ip_address");
        if ("1".equals(flag)) {
            try {
                String ip = InetAddress.getLocalHost().getHostAddress();
                if (!configIp.equals(ip)) {
                    //选定一台服务器做初始化的流程部署
                    return;
                }
                var resolver = new PathMatchingResourcePatternResolver();
                DeploymentBuilder builder = repositoryService.createDeployment();
                builder.name("init_process");
                var resources = resolver.getResources("classpath:bpmn/*.bpmn");
                for (var resource : resources) {
                    builder.addClasspathResource("bpmn/".concat(resource.getFilename()));
                }
                builder.deploy();
                log.info("init_process success");
            } catch (Exception e) {
                log.error("init_process fail", e);
            }
        }
    }


    /** ================================================================================== */
    /** ========================== 流程相关（RuntimeService）=============================== */
    /**
     * ==================================================================================
     */
    public String deployProcessFromFile(String originalFileName, InputStream bpmn_is) {
        Deployment deployment = repositoryService.createDeployment()
                .addInputStream(originalFileName, bpmn_is)
                .name(originalFileName)
                .deploy();
        return "";
    }

    /**
     * @param deploymentName
     * @param bpmnName       流程图文件及路径，bpmn/vacate.bpmn
     * @return
     */
    public String deployProcessFromClassPath(String deploymentName, String bpmnName) {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.name(deploymentName);
        deploymentBuilder.addClasspathResource(bpmnName);
        Deployment deployment = deploymentBuilder.deploy();
        return "";
    }

    public String deployProcessFromZip(String zipFile_path) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(zipFile_path);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream).deploy();
        return "";
    }

    /**
     * 启动流程实例
     *
     * @param processDefinitionKey
     * @param businessKey
     * @param processVariables
     * @param initiator
     * @return
     */
    public String startProcessInstance(String processDefinitionKey, String businessKey, Object processVariables, String initiator) {
        log.debug("start process instance, processDefinitionKey: {}, businessKey={}, processVariables: {}", processDefinitionKey, businessKey, processVariables);
        Map processVariablesMap =new HashMap();// CamundaTools.convertProcessVariablesFromEntity(processVariables);
        identityService.setAuthenticatedUserId(initiator);//设置流程发起人
        ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, processVariablesMap);
        log.debug("start process instance success, processInstanceId: {}", processInstance.getId());
        String result = null;
        if (processInstance != null) {
            result = processInstance.getId();
        }
        return result;
    }


    /**
     * 启动流程实例
     *
     * @param processDefinitionKey
     * @param businessKey
     * @return
     */
    public String startProcessInstance(String processDefinitionKey, String businessKey) {
        log.debug("start process instance, processDefinitionKey: {}, businessKey={}", processDefinitionKey, businessKey);
        ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        log.debug("start process instance success, processInstanceId: {}", processInstance.getId());
        String result = null;
        if (processInstance == null) {
            result = "未找到对应的流程，流程名为：" + processDefinitionKey;
        } else {
            result = "启动流程名为：" + processDefinitionKey + " ,流程Id为：" + processInstance.getId();
        }
        return "";
    }

    /**
     * 从任意节点启动实例
     *
     * @param processDefinitionKey
     * @param businessKey
     * @param activityId
     * @return
     */
    public String createProcessInstanceByKey(String processDefinitionKey, String businessKey, String activityId, String opCode) {

        identityService.setAuthenticatedUserId(opCode);

        ProcessInstantiationBuilder processInstantiationBuilder = runtimeService.createProcessInstanceByKey(processDefinitionKey);
        ProcessInstance processInstance = processInstantiationBuilder.businessKey(businessKey)
                //某节点之前开始
                .startBeforeActivity(activityId)
                .execute();
        String result = null;
        if (processInstance == null) {
            result = "未找到对应的流程，流程名为：" + processDefinitionKey;
        } else {
            result = "启动流程名为：" + processDefinitionKey + " ,流程Id为：" + processInstance.getId();
        }
        return "";
    }

    /**
     * 实例批量激活
     *
     * @param processInstanceIds
     * @param opCode
     * @return
     */
    public String batchUpdateProcessInstanceActivateState(List<String> processInstanceIds, String opCode) {

        identityService.setAuthenticatedUserId(opCode);

        runtimeService.updateProcessInstanceSuspensionState()
                .byProcessInstanceIds(processInstanceIds).activate();
        return "";
    }

    /**
     * 实例批量挂起
     *
     * @param processInstanceIds
     * @param opCode
     * @return
     */
    public String batchUpdateProcessInstanceSuspensionState(List<String> processInstanceIds, String opCode) {

        identityService.setAuthenticatedUserId(opCode);

        runtimeService.updateProcessInstanceSuspensionState()
                .byProcessInstanceIds(processInstanceIds).suspend();
        return "";
    }

    /**
     * 批量删除流程实例
     *
     * @param processInstanceIds
     * @param reason
     * @param opCode
     * @return
     */
    public String deleteProcessInstances(List<String> processInstanceIds, String reason, String opCode) {
        identityService.setAuthenticatedUserId(opCode);
        runtimeService.deleteProcessInstances(processInstanceIds, reason, true, true);
        return "";
    }

    /**
     * @param processDefinitionKey
     * @param businessKey
     * @param processVariables
     * @param opCode
     * @return
     */
    public String restartProcessInstances(String processDefinitionKey, String businessKey, Object processVariables, String opCode) {
        identityService.setAuthenticatedUserId(opCode);
        Map processVariablesMap = new HashMap();//CamundaTools.convertProcessVariablesFromEntity(processVariables);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionKey, businessKey, processVariablesMap);
        String result = null;
        if (processInstance == null) {
            result = "未找到对应的流程，流程名为：" + processDefinitionKey;
        } else {
            result = "重新启动流程名为：" + processDefinitionKey + " ,流程Id为：" + processInstance.getId() + "成功";
        }
        return "";
    }


    /**
     * 获取流程实例对应的流程变量集合
     *
     * @param processInstanceId
     * @return
     */
    public Map<String, Object> getRuntimeProcessVariables(String processInstanceId) {
        //获取流程变量
        List<VariableInstance> variableInstanceList = this.runtimeService.createVariableInstanceQuery()
                .processInstanceIdIn(processInstanceId)
                //.taskIdIn()
                .list();
        Map<String, Object> variableInstanceMap = new HashMap<>();//CamundaTools.convertVariableInstances(variableInstanceList);
        return variableInstanceMap;
    }

    /**
     * 根据变量名获取单个变量
     *
     * @param processInstanceId
     * @param variableName
     * @return
     */
    public Map<String, Object> getRuntimeSingleProcessVariable(String processInstanceId, String variableName) {
        List<VariableInstance> variableInstanceList = this.runtimeService.createVariableInstanceQuery()
                .processInstanceIdIn(processInstanceId)
                //.taskIdIn()
                .list();
        Map<String, Object> variableInstanceMap = new HashMap<>();//) CamundaTools.getVariableInstancesByName(variableInstanceList, variableName);
        return variableInstanceMap;
    }

//    /**
//     * 获取流程实例对应的流程变量集合
//     *
//     * @param processVariablesQueryParam
//     * @return
//     */
//    public Map<String, Object> getRuntimeProcessVariables(ProcessVariablesQueryParam processVariablesQueryParam) {
//        //创建查询
//        VariableInstanceQuery variableInstanceQuery = this.runtimeService.createVariableInstanceQuery();
////        CamundaTools.setNotNull(variableInstanceQuery::processInstanceIdIn, processVariablesQueryParam.getProcessInstanceId());
////        CamundaTools.setNotNull(variableInstanceQuery::taskIdIn, processVariablesQueryParam.getTaskId());
////        CamundaTools.setNotNull(variableInstanceQuery::variableName, processVariablesQueryParam.getVariableName());
//
//        //获取流程变量
//        List<VariableInstance> variableInstanceList = variableInstanceQuery.list();
//        Map<String, Object> variableInstanceMap =new HashMap<>();// CamundaTools.convertVariableInstances(variableInstanceList);
//        return variableInstanceMap;
//    }

    /**
     * 获取流程实例对应的流程变量集合
     *
     * @param processInstanceId
     * @return
     */
    public <T> T getRuntimeProcessVariables(String processInstanceId, String processVariableName) {
        //获取流程变量
        VariableInstance variableInstance = this.runtimeService.createVariableInstanceQuery()
                .processInstanceIdIn(processInstanceId)
                //.taskIdIn()
                .variableName(processVariableName)
                .singleResult();
        return (T) variableInstance.getValue();
    }


    /**
     * 获取流程实例对应的流程变量集合
     *
     * @param processInstanceId
     * @return
     */
    public Map<String, Object> getHistoryProcessVariables(String processInstanceId) {

        //获取流程变量
        List<HistoricVariableInstance> historicVariableInstanceList = this.historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                //.taskIdIn()
                .list();
        Map<String, Object> variableInstanceMap =new HashMap<>();// CamundaTools.convertHistoricVariableInstances(historicVariableInstanceList);
        return variableInstanceMap;
    }

    /** ================================================================================== */
    /** ========================== 任务相关（TaskService、HistoryService）================== */
    /** ================================================================================== */
    /**
     * 需要将任务分配给用户，一般有3种分配方式

     直接指定，这里通过Assignee来直接指定某一个具体的用户(一般是用户ID或者唯一的用户名)，支持表达式以支持动态指定

     指定候选人，通过candidateUser来指定一系列候选人，如果是多个用户，通过,号分隔

     指定候选组，通过candidateGroup来指定某一个组里面的所有用户(实际测试中，发现候选人和候选组是并集关系)

     如果指定了候选人和候选组，那么并不意味着所有的候选人都需要执行任务，这些人首先需要进行一个认领的操作，

     一个任务只能由一个人认领，认领完成后才能执行任务，相对的，也可以取消认领

     */

//    /**
//     * 查询待办任务
//     *
//     * @param taskQueryParam
//     * @return
//     */
//    public <T> PageInfo<TaskVo<T>> queryToDoTaskList(TaskQueryParam taskQueryParam, String taskBizKeyVariableName, Function<String, T> taskBizKey2BizTaskDataFunction) {
//        PageHelper.startPage(taskQueryParam.getPageNo(), taskQueryParam.getPageSize());
//        //创建查询
//        TaskQuery taskQuery = this.taskService.createTaskQuery();
//        CamundaTools.setNotNull(taskQuery::processDefinitionKey, taskQueryParam.getProcessDefinitionKey());
//        CamundaTools.setNotNull(taskQuery::taskDefinitionKey, taskQueryParam.getTaskDefinitionKey());
//        CamundaTools.setNotNull(taskQuery::taskAssignee, taskQueryParam.getAssignee());
//        BasePageParam basePageParam = new BasePageParam();
//        basePageParam.setPageNo(taskQueryParam.getPageNo());
//        basePageParam.setPageSize(taskQueryParam.getPageSize());
//        basePageParam.setOrderBy(taskQueryParam.getOrderBy());
//        basePageParam.setOrderSort(taskQueryParam.getOrderSort());
//        CamundaTools.setTaskQueryOrderBy(taskQuery, basePageParam);
//        //查询任务列表
//        List<Task> taskList = taskQuery.list();
//        PageInfo<Task> pageInfo = new PageInfo<>(taskList);
//        List<TaskVo<T>> taskVoList = taskList.stream()
//                .map(task -> {
//                    TaskVo<T> taskVo = CamundaTools.convertTask(task);
//                    return taskVo;
//                })
//                .map(taskVo -> {
//                    //设置流程变量
//                    this.setTaskProcessVariables(taskVo, taskQueryParam);
//                    //设置task变量（例如paymentId）对应的task业务数据
//                    //设置任务状态：取流程变量里的status变量，推流程时需要把任务状态存到status流程变量中
//                    if (taskVo.getProcessVariables() != null) {
//                        taskVo.setStatus((String) taskVo.getProcessVariables().get("status"));//设置任务状态
//                    }
//                    this.setTaskVoTaskData(taskVo, taskBizKeyVariableName, taskBizKey2BizTaskDataFunction);
//                    return taskVo;
//                })
//                .collect(Collectors.toList());
//        PageInfo<TaskVo<T>> pageInfoNew = PageUtils.PageInfo2PageInfoVo(pageInfo);
//        pageInfoNew.setList(taskVoList);
//        return pageInfoNew;
//    }

    /**
     * 查询历史任务
     *
     * @param pageNum
     * @param pageSize
     * @param assignee
     * @return
     */
    public String queryCompletedTaskList(int pageNum, int pageSize, String assignee) {
        PageHelper.startPage(pageNum, pageSize);
        List<HistoricTaskInstance> list = processEngine.getHistoryService() // 历史任务Service
                .createHistoricTaskInstanceQuery() // 创建历史任务实例查询
                .taskAssignee(assignee) // 指定办理人
                .finished() // 查询已经完成的任务
                .list();
        PageInfo pageInfo = new PageInfo(list);
        return "";
    }

    /**
     * 认领一个任务
     *
     * @param taskId
     * @param assignee 任务也可以进行分组：
     *                 //taskService.addCandidateGroup(taskId,groupId);//设置用户组角色，用户绑定组角色，则拥有相同组角色的用户可以查询任务
     * @return
     */
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

    /**
     * 设置任务组code，groupCode为任务可以处理的人员所在机构
     *
     * @param taskId
     * @param groupCode
     */
    public void setTaskGroup(String taskId, String groupCode) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            taskService.addCandidateGroup(taskId, groupCode);
        } else {
            throw new Exception("未查询到对应的task信息！");
        }
    }

    /**
     * 任务改派
     *
     * @param taskId
     * @param assignee
     * @return
     */
    public String setAssignee(String taskId, String assignee) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            taskService.setAssignee(task.getId(), assignee);
            return "";
        } else {
            return "";
        }
    }

    /**
     * 完成指定的任务
     *
     * @param taskId
     * @return
     */
    public String compeletTaskById(String taskId, Object processVariables) {
        Map processVariablesMap =new HashMap();// CamundaTools.convertProcessVariablesFromEntity(processVariables);
        taskService.complete(taskId, processVariablesMap);
        return "";
    }

    /**
     * 任务驳回到起始点
     *
     * @param taskId
     * @return
     */
    public String reject(String taskId) {
        //获取当前任务，未办理任务id
        HistoricTaskInstance currTask = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .singleResult();
        //获取流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(currTask.getProcessInstanceId())
                .singleResult();
        //获取流程定义
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(currTask.getProcessDefinitionId());
        ActivityImpl currActivity = (processDefinitionEntity).findActivity(currTask.getTaskDefinitionKey());
        //清除当前活动出口
        List<PvmTransition> originPvmTransitionList = new ArrayList<PvmTransition>();
        List<PvmTransition> pvmTransitionList = currActivity.getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            originPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();
        //查找上一个user task节点
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery().activityType("userTask")
                .processInstanceId(processInstance.getId())
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc().list();
        TransitionImpl transitionImpl = null;
        if (historicActivityInstances.size() > 0) {
            ActivityImpl lastActivity = (processDefinitionEntity)
                    .findActivity(historicActivityInstances.get(0).getActivityId());
            //创建当前任务的新出口
            transitionImpl = currActivity.createOutgoingTransition(lastActivity.getId());
            transitionImpl.setDestination(lastActivity);
        }
        // 完成任务
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey(currTask.getTaskDefinitionKey()).list();
        for (Task task : tasks) {
            taskService.complete(task.getId());
            historyService.deleteHistoricTaskInstance(task.getId());
        }
        // 恢复方向
        currActivity.getOutgoingTransitions().remove(transitionImpl);
        for (PvmTransition pvmTransition : originPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }
        return "";
    }

    /**
     * 开始节点提交并推动任务流到下一个节点，可在下一个任务节点中使用该任务的taskId获取开始节点提交的表单数据
     * processDefinitionId是部署后生成(key:version:deploymentid)
     * 存放在act_re_deployment表id字段
     *
     * @param processDefinitionId
     * @return
     */
    public String submitStartForm(String processDefinitionId) {
        VariableMap variableMap = Variables.createVariables()
                .putValue("startDate", "20211230")
                .putValue("endDate", "20211231")
                .putValue("reason", "调休")
                .putValue("employee", "ljj");
        FormService formService = processEngine.getFormService();
        ProcessInstance processInstance = formService.submitStartForm(processDefinitionId, variableMap);
        return "";
    }

    /**
     * 提交任务节点并保存表单
     * act_ru_variable
     *
     * @param taskId
     * @return
     */
    public String submitTaskForm(String taskId) {
        VariableMap variableMap = Variables.createVariables()
                .putValue("startDate", "20211230")
                .putValue("endDate", "20211231")
                .putValue("reason", "调休");
        FormService formService = processEngine.getFormService();
        formService.submitTaskForm(taskId, variableMap);
        return "";
    }

    public String getStartFormData(String processDefinitionId) {
        FormService formService = processEngine.getFormService();
        StartFormData startFormData = formService.getStartFormData(processDefinitionId);
        List<FormField> formFields = startFormData.getFormFields();
        for (FormField formField : formFields) {
            log.info("############");
            log.info(formField.getId());
            log.info(String.valueOf(formField.getValue()));
            log.info(String.valueOf(formField.getType()));
            log.info(formField.getTypeName());
            log.info(formField.getLabel());
            log.info(String.valueOf(formField.getDefaultValue()));
            log.info("############");
        }
        return "";
    }

    public String getTaskFormData(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            FormService formService = processEngine.getFormService();
            TaskFormData taskFormData = formService.getTaskFormData(task.getId());
            log.info("############");
            log.info(taskFormData.getDeploymentId());
            log.info(String.valueOf(taskFormData.getTask()));
            log.info(taskFormData.getFormKey());
            List<FormProperty> formProperties = taskFormData.getFormProperties();
            log.info(String.valueOf(formProperties));
            List<FormField> formFields = taskFormData.getFormFields();
            log.info(String.valueOf(formFields));
            log.info("############");
        }
        return "";
    }

    /**
     * 根据taskId查找任务信息
     *
     * @param taskId
     * @return
     */
    public Task queryTaskByTaskId(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        return task;
    }

    /**
     * 撤销，流程发起后，发起人后来觉得没必要发起这样的流程，这个时候就想取消发起的流程
     * 验证。 具体要验证什么呢？包含当前流程实例状态、当前执行人是否为流程发起人、验证当前活动实例
     * 取消产生的任务  查找此流程产生的任务并取消
     * 删除此流程实例
     */

    public int withDraw(String processInstanceId, String assignee) {
        int state = checkProcessInstanceState(processInstanceId);
        if (state != 0) {
            return state;
        }

        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (CollectionUtils.isEmpty(taskList)) {
            return 1;
        }

        // 判断当前执行人是否为流程发起人
        /*state = checkProcessStarter(procId, userInfo);
        if( state != 0 ){
            return state;
        }*/

        Task task = taskList.get(0);
        List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .activityType("userTask")
                .finished().orderByHistoricActivityInstanceEndTime()
                .asc().list();

        if (historicActivityInstanceList == null || historicActivityInstanceList.isEmpty()) {
            return 2;
        }

        ActivityInstance activityInstance = runtimeService.getActivityInstance(task.getProcessInstanceId());
        String toActId = historicActivityInstanceList.get(0).getActivityId();
        Map<String, Object> taskVariable = new HashMap<>();
        //设置当前处理人
        taskVariable.put("assignee", assignee);//TODO map中的key可能要修改

        runtimeService.createProcessInstanceModification(task.getProcessInstanceId())
                //关闭相关任务
                .cancelActivityInstance(getInstanceIdForActivity(activityInstance, task.getTaskDefinitionKey()))
                .setAnnotation("进行了撤回到节点操作")
                //启动目标活动节点
                .startBeforeActivity(toActId)
                //流程的可变参数赋值
                .setVariables(taskVariable)
                .execute();

        runtimeService.deleteProcessInstance(task.getProcessInstanceId(), String.format("%s 用户执行了撤回操作", assignee));

        return 0;

    }

    public String jumpNode(String processInstanceId, String targetActivityId) {
        // processInstanceId 整个流程的流程实例Id
        // targetActivityId 需要跳转到的节点Id（可以是任意节点）
        ProcessInstanceModificationBuilder processInstanceModificationBuilder = runtimeService.createProcessInstanceModification(processInstanceId);
        Set<String> activityIdSet = new HashSet<>();
        taskService.createTaskQuery().processInstanceId(processInstanceId).active().list().forEach(taskQuery -> {
            String activityId = taskQuery.getTaskDefinitionKey();
            if (activityIdSet.add(activityId)) {
                processInstanceModificationBuilder.cancelAllForActivity(activityId);
            }
        });
        processInstanceModificationBuilder.startBeforeActivity(targetActivityId)
                .execute();

        log.info("流程处理成功");
        return "";
    }

    /**
     * 流程实例Id
     *
     * @param processInstanceId
     * @return
     */
    public String deleteProcessInstance(String processInstanceId, String deleteReason) {
        runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
        historyService.deleteHistoricProcessInstance(processInstanceId);
        return "";
    }


    public String taskGetComment(String processInstanceId, String assignee) {
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .taskAssignee(assignee)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();
        List<Map<String, Object>> result = new ArrayList<>(list.size());
        Map<String, Object> map = null;
        List<Comment> taskComments = null;
        for (HistoricActivityInstance historicActivityInstance : list) {
            map = new HashMap<>(5);
            String taskId = historicActivityInstance.getTaskId();
            taskComments = taskService.getTaskComments(taskId);
            log.info(taskComments.size() + " ");
            map.put("activityName", historicActivityInstance.getActivityName());
            map.put("activityType", matching(historicActivityInstance.getActivityType()));
            map.put("assignee", historicActivityInstance.getAssignee() == null ? "无" : historicActivityInstance.getAssignee());
            map.put("startTime", DateFormatUtils.format(historicActivityInstance.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("endTime", DateFormatUtils.format(historicActivityInstance.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("costTime", getDatePoor(historicActivityInstance.getEndTime(), historicActivityInstance.getStartTime()));

            if (taskComments.size() > 0) {
                map.put("message", taskComments.get(0).getFullMessage());
            } else {
                map.put("message", "无");
            }
            result.add(map);
        }
        return "";
    }

    private String matching(String ActivityType) {
        String value = "";
        switch (ActivityType) {
            case "startEvent":
                value = "流程开始";
                break;
            case "userTask":
                value = "用户处理";
                break;
            case "noneEndEvent":
                value = "流程结束";
                break;
            default:
                value = "未知节点";
                break;
        }
        return value;
    }

    public String getDatePoor(Date endDate, Date nowDate) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟" + sec + "秒";
    }

    protected int checkProcessInstanceState(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (ObjectUtils.isEmpty(processInstance)) {
            return 5;
        }
        if (processInstance.isEnded()) {
            return 6;
        }
        return 0;
    }

    protected String getInstanceIdForActivity(ActivityInstance activityInstance, String activityId) {
        ActivityInstance instance = getChildInstanceForActivity(activityInstance, activityId);
        if (instance != null) {
            return instance.getId();
        }
        return null;
    }

    protected ActivityInstance getChildInstanceForActivity(ActivityInstance activityInstance, String activityId) {
        if (activityId.equals(activityInstance.getActivityId())) {
            return activityInstance;
        }
        for (ActivityInstance childInstance : activityInstance.getChildActivityInstances()) {
            ActivityInstance instance = getChildInstanceForActivity(childInstance, activityId);
            if (instance != null) {
                return instance;
            }
        }
        return null;
    }

//    /**
//     * 设置task对应的业务任务数据
//     * task -> task.processVariables[taskBizDataVariableName] -> bizTaskData
//     *
//     * @param taskVo
//     * @param taskBizDataVariableName
//     * @param processVariable2BizTaskDataFunction
//     * @param <M>
//     */
//    private <M> void setTaskVoTaskData(TaskVo taskVo, String taskBizDataVariableName,
//                                       Function<String, M> processVariable2BizTaskDataFunction) {
//        if (null == taskBizDataVariableName || null == processVariable2BizTaskDataFunction) {
//            return;
//        }
//        String taskDataIdValue = null;
//        if (null != taskVo.getProcessVariables() && taskVo.getProcessVariables().containsKey(taskBizDataVariableName)) {
//            taskDataIdValue = String.valueOf(taskVo.getProcessVariables().get(taskBizDataVariableName));
//        } else {
//            taskDataIdValue = this.getRuntimeProcessVariables(taskVo.getProcessInstanceId(), taskBizDataVariableName);
//        }
//        taskVo.setBizTaskData(processVariable2BizTaskDataFunction.apply(taskDataIdValue));
//    }

//    /**
//     * 设置task对应流程变量
//     *
//     * @param taskVo
//     * @param taskQueryParam
//     */
//    private void setTaskProcessVariables(TaskVo taskVo, TaskQueryParam taskQueryParam) {
//        //设置流程变量
//        if (taskQueryParam.getWithProcessVariables()) {
//            taskVo.setProcessVariables(this.getRuntimeProcessVariables(taskVo.getProcessInstanceId()));
//        }
//    }

    public String getNextTaskJsonInfos(String processInstanceId, Map<String, Object> condition) throws Exception {
        List<TaskDefinition> tasks = getNextTaskInfos(processInstanceId, condition);
        JSONArray ja = new JSONArray();
        if (!CollectionUtils.isEmpty(tasks)) {
            JSONObject jo = null;
            for (TaskDefinition task : tasks) {
                jo = new JSONObject();
                jo.putOnce("key", task.getKey());
                jo.putOnce("name", task.getNameExpression().getExpressionText());
                jo.putOnce("assignment", task.getAssigneeExpression() == null ? "" : task.getAssigneeExpression().getExpressionText());
                ja.add(jo);
            }
        }
        return ja.toString();
    }

    /**
     * 获取下一个节点任务信息
     *
     * @param processInstanceId 流程实例ID
     * @return 下一个节点信息
     * @throws Exception
     */
    public List<TaskDefinition> getNextTaskInfos(String processInstanceId, Map<String, Object> condition) throws Exception {
        try {
            ProcessDefinitionEntity processDefinitionEntity = null;
            String id = null;
            // List<TaskDefinition> tasks =
            // null;//会出现java.util.ConcurrentModificationException异常，改成CopyOnWriteArrayList
            List<TaskDefinition> tasks = new CopyOnWriteArrayList<TaskDefinition>();
            // 获取流程发布Id信息
            String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                    .singleResult().getProcessDefinitionId();
            processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                    .getDeployedProcessDefinition(definitionId);
            ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
                    .executionId(processInstanceId).singleResult();
            // 当前流程节点Id信息
            String activitiId = execution.getActivityId();
            log.info("execution----->" + activitiId);
            // 获取流程所有节点信息
            List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();

            JSONArray ja = new JSONArray();
            // 遍历所有节点信息
            for (ActivityImpl activityImpl : activitiList) {
                id = activityImpl.getId();
                log.info("id----------->" + id + "----->" + activityImpl.getProperty("type"));
                if (activitiId.equals(id)) {
                    // 获取下一个节点信息
                    tasks = nextTaskDefinitions(activityImpl, activityImpl.getId(), processInstanceId, condition);
                    break;
                }
            }
            return tasks;
        } catch (Exception e) {
            log.error("operation error", e);
            throw e;
        }
    }

    private List<TaskDefinition> nextTaskDefinitions(ActivityImpl activityImpl, String activityId,
                                                     String processInstanceId, Map<String, Object> condition) {
        try {
            PvmActivity ac = null;
            Object s = null;
            log.info("activityImpl.getActivityId()---->" + activityImpl.getActivityId() + "---activityId---------->" + activityId + "--->" + activityImpl.getProperty("type"));
            // List<TaskDefinition> taskDefinitions = new
            // ArrayList<TaskDefinition>();
            // //会出现java.util.ConcurrentModificationException异常，改成CopyOnWriteArrayList
            List<TaskDefinition> taskDefinitions = new CopyOnWriteArrayList<TaskDefinition>();
            // 如果遍历节点为用户任务并且节点不是当前节点信息
            if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
                // 获取该节点下一个节点信息
                TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior())
                        .getTaskDefinition();
                taskDefinitions.add(taskDefinition);
            } else if (activityImpl.getProperty("type").toString().contains("EndEvent") && !activityId.equals(activityImpl.getId())) {
                // 设置结束节点
                TaskDefinition taskDefinition = new TaskDefinition(null);
                ExpressionManager expressionManager = new ExpressionManager() {
                    @Override
                    public Expression createExpression(String s) {
                        return null;
                    }

                    @Override
                    public void addFunction(String s, Method method) {

                    }
                };
                taskDefinition.setKey(activityImpl.getId() == null ? "end" : activityImpl.getId());
                String name = activityImpl.getProperty("name") == null ? "结束" : activityImpl.getProperty("name").toString();
                taskDefinition.setNameExpression(expressionManager.createExpression(name));
                taskDefinitions.add(taskDefinition);
            } else if ("multiInstanceBody".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
                // 获取该节点下一个节点信息
                List<ActivityImpl> list = ((ActivityImpl) activityImpl).getActivities();
                for (ActivityImpl act : list) {
                    //System.out.println("act-------------->"+act.getActivityBehavior().getClass().getTypeName());
                    TaskDefinition taskDefinition = ((UserTaskActivityBehavior) act.getActivityBehavior())
                            .getTaskDefinition();
                    taskDefinitions.add(taskDefinition);
                }
            } else if ("exclusiveGateway".equals(activityImpl.getProperty("type"))
                    || "inclusiveGateway".equals(activityImpl.getProperty("type"))) {// 当前节点为exclusiveGateway或inclusiveGateway
                List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
                String defaultTransition = (String) activityImpl.getProperty("default");
                if (outTransitions.size() == 1) {
                    taskDefinitions.addAll(nextTaskDefinitions((ActivityImpl) outTransitions.get(0).getDestination(),
                            activityId, processInstanceId, condition));
                } else if (outTransitions.size() > 1) { // 如果排他网关有多条线路信息
                    method2(activityId, processInstanceId, condition, taskDefinitions, outTransitions, defaultTransition);
                }
            } else if ("parrallelGateway".equals(activityImpl.getProperty("type"))) {
                List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
                for (PvmTransition tr1 : outTransitions) {
                    taskDefinitions.addAll(nextTaskDefinitions((ActivityImpl) tr1.getDestination(), activityId,
                            processInstanceId, condition));
                }
            } else {
                method3(activityImpl, activityId, processInstanceId, condition, taskDefinitions);
            }
            return taskDefinitions;

        } catch (Exception e) {
            log.error("operation error", e);
            throw e;
        }
    }

    private void method2(String activityId, String processInstanceId, Map<String, Object> condition, List<TaskDefinition> taskDefinitions, List<PvmTransition> outTransitions, String defaultTransition) {
        Object s;
        ActivityImpl actImpl = null;
        TaskDefinition taskDefinition = null;
        ExpressionManager expressionManager = null;
        for (PvmTransition tr1 : outTransitions) {
            actImpl = (ActivityImpl) tr1.getDestination();
            if (actImpl.getProperty("type").toString().contains("EndEvent")) {
                taskDefinition = new TaskDefinition(null);
                expressionManager = new ExpressionManager() {
                    @Override
                    public Expression createExpression(String s) {
                        return null;
                    }

                    @Override
                    public void addFunction(String s, Method method) {

                    }
                };
                taskDefinition.setKey(actImpl.getId() == null ? "end" : actImpl.getId());
                String name = actImpl.getProperty("name") == null ? "结束"
                        : actImpl.getProperty("name").toString();
                taskDefinition.setNameExpression(expressionManager.createExpression(name));
                taskDefinitions.add(taskDefinition);
                break;
            }
            s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
            if (null == s) {
                continue;
            }
            // 判断el表达式是否成立
            if (isCondition(condition, StringUtils.trim(s.toString()))) {
                taskDefinitions.addAll(nextTaskDefinitions((ActivityImpl) tr1.getDestination(), activityId,
                        processInstanceId, condition));
            }
        }
        if (taskDefinitions.size() == 0 && StringUtils.isNotBlank(defaultTransition)) {


            extracted1034(activityId, processInstanceId, condition, taskDefinitions, outTransitions, defaultTransition);
        }
    }

    private void method3(ActivityImpl activityImpl, String activityId, String processInstanceId, Map<String, Object> condition, List<TaskDefinition> taskDefinitions) {
        PvmActivity ac;
        // 获取节点所有流向线路信息
        List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
        List<PvmTransition> outTransitionsTemp = null;
        TaskDefinition taskDefinition = null;
        ExpressionManager expressionManager = null;
        for (PvmTransition tr : outTransitions) {
            ac = tr.getDestination(); // 获取线路的终点节点
            log.info("ac----------->" + ac.getId() + "------>" + ac.getProperty("type"));
            // 如果流向线路为排他网关或包容网关
            if ("exclusiveGateway".equals(ac.getProperty("type"))
                    || "inclusiveGateway".equals(ac.getProperty("type"))) {
                outTransitionsTemp = ac.getOutgoingTransitions();
                String defaultTransition = (String) ac.getProperty("default");
                // 如果排他网关只有一条线路信息
                if (outTransitionsTemp.size() == 1) {
                    taskDefinitions.addAll(
                            nextTaskDefinitions((ActivityImpl) outTransitionsTemp.get(0).getDestination(),
                                    activityId, processInstanceId, condition));
                } else if (outTransitionsTemp.size() > 1) { // 如果排他网关有多条线路信息
                    extracted1095(activityId, processInstanceId, condition, taskDefinitions, outTransitionsTemp, defaultTransition);
                }
            } else if ("userTask".equals(ac.getProperty("type"))) {
                taskDefinitions.add(((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior())
                        .getTaskDefinition());
            } else if ("multiInstanceBody".equals(ac.getProperty("type"))) {
                List<ActivityImpl> list = ((ActivityImpl) ac).getActivities();
                for (ActivityImpl act : list) {
                    //System.out.println("act-------------->"+act.getActivityBehavior().getClass().getTypeName());
                    taskDefinition = ((UserTaskActivityBehavior) act.getActivityBehavior())
                            .getTaskDefinition();
                    taskDefinitions.add(taskDefinition);
                }
            } else if (ac.getProperty("type").toString().contains("EndEvent")) {
                // 设置结束节点
                taskDefinition = new TaskDefinition(null);
                expressionManager = new ExpressionManager() {
                    @Override
                    public Expression createExpression(String s) {
                        return null;
                    }

                    @Override
                    public void addFunction(String s, Method method) {

                    }
                };
                taskDefinition.setKey(ac.getId() == null ? "end" : ac.getId());
                String name = ac.getProperty("name") == null ? "结束" : ac.getProperty("name").toString();
                taskDefinition.setNameExpression(expressionManager.createExpression(name));
                taskDefinitions.add(taskDefinition);
            } else if ("parrallelGateway".equals(ac.getProperty("type"))) {
                List<PvmTransition> poutTransitions = ac.getOutgoingTransitions();
                for (PvmTransition tr1 : poutTransitions) {
                    taskDefinitions.addAll(nextTaskDefinitions((ActivityImpl) tr1.getDestination(), activityId,
                            processInstanceId, condition));
                }
            }
        }
    }

    private void extracted1034(String activityId, String processInstanceId, Map<String, Object> condition, List<TaskDefinition> taskDefinitions, List<PvmTransition> outTransitions, String defaultTransition) {
        TaskDefinition taskDefinition2 = null;
        ExpressionManager expressionManager2 = null;
        for (PvmTransition tr3 : outTransitions) {
            if (defaultTransition.equals(tr3.getId())) {
                ActivityImpl actImpl = (ActivityImpl) tr3.getDestination();
                if (actImpl.getProperty("type").toString().contains("EndEvent")) {
                    taskDefinition2 = new TaskDefinition(null);
                    expressionManager2 = new ExpressionManager() {
                        @Override
                        public Expression createExpression(String s) {
                            return null;
                        }

                        @Override
                        public void addFunction(String s, Method method) {

                        }
                    };
                    taskDefinition2.setKey(actImpl.getId() == null ? "end" : actImpl.getId());
                    String name2 = actImpl.getProperty("name") == null ? "结束"
                            : actImpl.getProperty("name").toString();
                    taskDefinition2.setNameExpression(expressionManager2.createExpression(name2));
                    taskDefinitions.add(taskDefinition2);
                    break;
                }

                taskDefinitions.addAll(nextTaskDefinitions(actImpl,
                        activityId, processInstanceId, condition));
                log.info("taskDefinitions---333333333--->" + taskDefinitions.size());
            }
        }
    }

    private void extracted1095(String activityId, String processInstanceId, Map<String, Object> condition, List<TaskDefinition> taskDefinitions, List<PvmTransition> outTransitionsTemp, String defaultTransition) {
        Object s;
        ActivityImpl actImpl = null;
        TaskDefinition taskDefinition2 =  null;
        ExpressionManager expressionManager2 =  null;
        for (PvmTransition tr1 : outTransitionsTemp) {
            actImpl = (ActivityImpl) tr1.getDestination();
            if (actImpl.getProperty("type").toString().contains("EndEvent")) {
                 taskDefinition2 = new TaskDefinition(null);
                expressionManager2 = new ExpressionManager() {
                    @Override
                    public Expression createExpression(String s) {
                        return null;
                    }

                    @Override
                    public void addFunction(String s, Method method) {

                    }
                };
                taskDefinition2.setKey(actImpl.getId() == null ? "end" : actImpl.getId());
                String name2 = actImpl.getProperty("name") == null ? "结束"
                        : actImpl.getProperty("name").toString();
                taskDefinition2.setNameExpression(expressionManager2.createExpression(name2));
                taskDefinitions.add(taskDefinition2);
                break;
            }

            log.info("taskDefinitions--1111---->" + taskDefinitions.size());
            s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
            if (null == s) {
                continue;
            }
            // 判断el表达式是否成立
            if (isCondition(condition, StringUtils.trim(s.toString()))) {
                taskDefinitions.addAll(nextTaskDefinitions(actImpl, activityId, processInstanceId, condition));
            }

            log.info("taskDefinitions---22222--->" + taskDefinitions.size());
        }
        if (taskDefinitions.size() == 0 && StringUtils.isNotBlank(defaultTransition)) {
            extracted1102(activityId, processInstanceId, condition, taskDefinitions, outTransitionsTemp, defaultTransition);
        }
    }

    private void extracted1102(String activityId, String processInstanceId, Map<String, Object> condition, List<TaskDefinition> taskDefinitions, List<PvmTransition> outTransitionsTemp, String defaultTransition) {
        for (PvmTransition tr3 : outTransitionsTemp) {
            if (defaultTransition.equals(tr3.getId())) {
                ActivityImpl actImpl = (ActivityImpl) tr3.getDestination();
                if (actImpl.getProperty("type").toString().contains("EndEvent")) {
                    TaskDefinition taskDefinition2 = new TaskDefinition(null);
                    ExpressionManager expressionManager2 = new ExpressionManager() {
                        @Override
                        public Expression createExpression(String s) {
                            return null;
                        }

                        @Override
                        public void addFunction(String s, Method method) {

                        }
                    };
                    taskDefinition2.setKey(actImpl.getId() == null ? "end" : actImpl.getId());
                    String name2 = actImpl.getProperty("name") == null ? "结束"
                            : actImpl.getProperty("name").toString();
                    taskDefinition2.setNameExpression(expressionManager2.createExpression(name2));
                    taskDefinitions.add(taskDefinition2);
                    break;
                }

                taskDefinitions.addAll(nextTaskDefinitions(actImpl,
                        activityId, processInstanceId, condition));
                log.info("taskDefinitions---333333333--->" + taskDefinitions.size());
            }
        }
    }


    //region nextTaskDefinitions backup
//    private List<TaskDefinition> nextTaskDefinitions(ActivityImpl activityImpl, String activityId,
//                                                     String processInstanceId, Map<String, Object> condition) {
//        try {
//            PvmActivity ac = null;
//            Object s = null;
//            log.info("activityImpl.getActivityId()---->" + activityImpl.getActivityId() + "---activityId---------->" + activityId + "--->" + activityImpl.getProperty("type"));
//            // List<TaskDefinition> taskDefinitions = new
//            // ArrayList<TaskDefinition>();
//            // //会出现java.util.ConcurrentModificationException异常，改成CopyOnWriteArrayList
//            List<TaskDefinition> taskDefinitions = new CopyOnWriteArrayList<TaskDefinition>();
//            // 如果遍历节点为用户任务并且节点不是当前节点信息
//            if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
//                // 获取该节点下一个节点信息
//                TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior())
//                        .getTaskDefinition();
//                taskDefinitions.add(taskDefinition);
//            } else if (activityImpl.getProperty("type").toString().contains("EndEvent") && !activityId.equals(activityImpl.getId())) {
//                // 设置结束节点
//                TaskDefinition taskDefinition = new TaskDefinition(null);
//                ExpressionManager expressionManager = new ExpressionManager();
//                taskDefinition.setKey(activityImpl.getId() == null ? "end" : activityImpl.getId());
//                String name = activityImpl.getProperty("name") == null ? "结束" : activityImpl.getProperty("name").toString();
//                taskDefinition.setNameExpression(expressionManager.createExpression(name));
//                taskDefinitions.add(taskDefinition);
//            } else if ("multiInstanceBody".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
//                // 获取该节点下一个节点信息
//                List<ActivityImpl> list = ((ActivityImpl) activityImpl).getActivities();
//                for (ActivityImpl act : list) {
//                    //System.out.println("act-------------->"+act.getActivityBehavior().getClass().getTypeName());
//                    TaskDefinition taskDefinition = ((UserTaskActivityBehavior) act.getActivityBehavior())
//                            .getTaskDefinition();
//                    taskDefinitions.add(taskDefinition);
//                }
//            } else if ("exclusiveGateway".equals(activityImpl.getProperty("type"))
//                    || "inclusiveGateway".equals(activityImpl.getProperty("type"))) {// 当前节点为exclusiveGateway或inclusiveGateway
//                List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
//                String defaultTransition = (String) activityImpl.getProperty("default");
//                if (outTransitions.size() == 1) {
//                    taskDefinitions.addAll(nextTaskDefinitions((ActivityImpl) outTransitions.get(0).getDestination(),
//                            activityId, processInstanceId, condition));
//                } else if (outTransitions.size() > 1) { // 如果排他网关有多条线路信息
//                    for (PvmTransition tr1 : outTransitions) {
//                        ActivityImpl actImpl = (ActivityImpl) tr1.getDestination();
//                        if (actImpl.getProperty("type").toString().contains("EndEvent")) {
//                            TaskDefinition taskDefinition = new TaskDefinition(null);
//                            ExpressionManager expressionManager = new ExpressionManager();
//                            taskDefinition.setKey(actImpl.getId() == null ? "end" : actImpl.getId());
//                            String name = actImpl.getProperty("name") == null ? "结束"
//                                    : actImpl.getProperty("name").toString();
//                            taskDefinition.setNameExpression(expressionManager.createExpression(name));
//                            taskDefinitions.add(taskDefinition);
//                            break;
//                        }
//                        s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
//                        if (null == s) {
//                            continue;
//                        }
//                        // 判断el表达式是否成立
//                        if (isCondition(condition, StringUtils.trim(s.toString()))) {
//                            taskDefinitions.addAll(nextTaskDefinitions((ActivityImpl) tr1.getDestination(), activityId,
//                                    processInstanceId, condition));
//                        }
//                    }
//                    if (taskDefinitions.size() == 0 && StringUtils.isNotBlank(defaultTransition)) {
//
//
//                        TaskDefinition taskDefinition2 = null;
//                        ExpressionManager expressionManager2 = null;
//                        for (PvmTransition tr3 : outTransitions) {
//                            if (defaultTransition.equals(tr3.getId())) {
//                                ActivityImpl actImpl = (ActivityImpl) tr3.getDestination();
//                                if (actImpl.getProperty("type").toString().contains("EndEvent")) {
//                                    taskDefinition2 = new TaskDefinition(null);
//                                    expressionManager2 = new ExpressionManager();
//                                    taskDefinition2.setKey(actImpl.getId() == null ? "end" : actImpl.getId());
//                                    String name2 = actImpl.getProperty("name") == null ? "结束"
//                                            : actImpl.getProperty("name").toString();
//                                    taskDefinition2.setNameExpression(expressionManager2.createExpression(name2));
//                                    taskDefinitions.add(taskDefinition2);
//                                    break;
//                                }
//
//                                taskDefinitions.addAll(nextTaskDefinitions(actImpl,
//                                        activityId, processInstanceId, condition));
//                                log.info("taskDefinitions---333333333--->" + taskDefinitions.size());
//                            }
//                        }
//                    }
//                }
//            } else if ("parrallelGateway".equals(activityImpl.getProperty("type"))) {
//                List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
//                for (PvmTransition tr1 : outTransitions) {
//                    taskDefinitions.addAll(nextTaskDefinitions((ActivityImpl) tr1.getDestination(), activityId,
//                            processInstanceId, condition));
//                }
//            } else {
//                // 获取节点所有流向线路信息
//                List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
//                List<PvmTransition> outTransitionsTemp = null;
//                for (PvmTransition tr : outTransitions) {
//                    ac = tr.getDestination(); // 获取线路的终点节点
//                    log.info("ac----------->" + ac.getId() + "------>" + ac.getProperty("type"));
//                    // 如果流向线路为排他网关或包容网关
//                    if ("exclusiveGateway".equals(ac.getProperty("type"))
//                            || "inclusiveGateway".equals(ac.getProperty("type"))) {
//                        outTransitionsTemp = ac.getOutgoingTransitions();
//                        String defaultTransition = (String) ac.getProperty("default");
//                        // 如果排他网关只有一条线路信息
//                        if (outTransitionsTemp.size() == 1) {
//                            taskDefinitions.addAll(
//                                    nextTaskDefinitions((ActivityImpl) outTransitionsTemp.get(0).getDestination(),
//                                            activityId, processInstanceId, condition));
//                        } else if (outTransitionsTemp.size() > 1) { // 如果排他网关有多条线路信息
//                            for (PvmTransition tr1 : outTransitionsTemp) {
//                                ActivityImpl actImpl = (ActivityImpl) tr1.getDestination();
//                                if (actImpl.getProperty("type").toString().contains("EndEvent")) {
//                                    TaskDefinition taskDefinition2 = new TaskDefinition(null);
//                                    ExpressionManager expressionManager2 = new ExpressionManager();
//                                    taskDefinition2.setKey(actImpl.getId() == null ? "end" : actImpl.getId());
//                                    String name2 = actImpl.getProperty("name") == null ? "结束"
//                                            : actImpl.getProperty("name").toString();
//                                    taskDefinition2.setNameExpression(expressionManager2.createExpression(name2));
//                                    taskDefinitions.add(taskDefinition2);
//                                    break;
//                                }
//
//                                log.info("taskDefinitions--1111---->" + taskDefinitions.size());
//                                s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
//                                if (null == s) {
//                                    continue;
//                                }
//                                // 判断el表达式是否成立
//                                if (isCondition(condition, StringUtils.trim(s.toString()))) {
//                                    taskDefinitions.addAll(nextTaskDefinitions(actImpl, activityId, processInstanceId, condition));
//                                }
//
//                                log.info("taskDefinitions---22222--->" + taskDefinitions.size());
//                            }
//                            if (taskDefinitions.size() == 0 && StringUtils.isNotBlank(defaultTransition)) {
//                                for (PvmTransition tr3 : outTransitionsTemp) {
//                                    if (defaultTransition.equals(tr3.getId())) {
//                                        ActivityImpl actImpl = (ActivityImpl) tr3.getDestination();
//                                        if (actImpl.getProperty("type").toString().contains("EndEvent")) {
//                                            TaskDefinition taskDefinition2 = new TaskDefinition(null);
//                                            ExpressionManager expressionManager2 = new ExpressionManager();
//                                            taskDefinition2.setKey(actImpl.getId() == null ? "end" : actImpl.getId());
//                                            String name2 = actImpl.getProperty("name") == null ? "结束"
//                                                    : actImpl.getProperty("name").toString();
//                                            taskDefinition2.setNameExpression(expressionManager2.createExpression(name2));
//                                            taskDefinitions.add(taskDefinition2);
//                                            break;
//                                        }
//
//                                        taskDefinitions.addAll(nextTaskDefinitions(actImpl,
//                                                activityId, processInstanceId, condition));
//                                        log.info("taskDefinitions---333333333--->" + taskDefinitions.size());
//                                    }
//                                }
//                            }
//                        }
//                    } else if ("userTask".equals(ac.getProperty("type"))) {
//                        taskDefinitions.add(((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior())
//                                .getTaskDefinition());
//                    } else if ("multiInstanceBody".equals(ac.getProperty("type"))) {
//                        List<ActivityImpl> list = ((ActivityImpl) ac).getActivities();
//                        for (ActivityImpl act : list) {
//                            //System.out.println("act-------------->"+act.getActivityBehavior().getClass().getTypeName());
//                            TaskDefinition taskDefinition = ((UserTaskActivityBehavior) act.getActivityBehavior())
//                                    .getTaskDefinition();
//                            taskDefinitions.add(taskDefinition);
//                        }
//                    } else if (ac.getProperty("type").toString().contains("EndEvent")) {
//                        // 设置结束节点
//                        TaskDefinition taskDefinition = new TaskDefinition(null);
//                        ExpressionManager expressionManager = new ExpressionManager();
//                        taskDefinition.setKey(ac.getId() == null ? "end" : ac.getId());
//                        String name = ac.getProperty("name") == null ? "结束" : ac.getProperty("name").toString();
//                        taskDefinition.setNameExpression(expressionManager.createExpression(name));
//                        taskDefinitions.add(taskDefinition);
//                    } else if ("parrallelGateway".equals(ac.getProperty("type"))) {
//                        List<PvmTransition> poutTransitions = ac.getOutgoingTransitions();
//                        for (PvmTransition tr1 : poutTransitions) {
//                            taskDefinitions.addAll(nextTaskDefinitions((ActivityImpl) tr1.getDestination(), activityId,
//                                    processInstanceId, condition));
//                        }
//                    }
//                }
//            }
//            return taskDefinitions;
//
//        } catch (Exception e) {
//            log.error("operation error", e);
//            throw e;
//        }
//    }

    //endregion

    private boolean isCondition(Map<String, Object> condition, String el) {
        try {
            ExpressionFactory factory = new ExpressionFactoryImpl();
            SimpleContext context = new SimpleContext();
            if (condition != null) {
                Iterator<Map.Entry<String, Object>> iterator = condition.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> value = iterator.next();
                    context.setVariable(value.getKey(), factory.createValueExpression(value.getValue(), String.class));
                }
            }
            ValueExpression e = factory.createValueExpression(context, el, boolean.class);
            return (Boolean) e.getValue(context);
        } catch (Exception e) {
            log.error("operation error", e);
            throw e;
        }
    }

    /**
     * 加签方法
     *
     * @param processInstanceId
     * @param activityId
     */
    public void addInstance(String processInstanceId, String activityId, String assignee) {

        runtimeService.createProcessInstanceModification(processInstanceId)
                .startBeforeActivity(activityId)//会签节点的activityId
                .setVariable("assignee", assignee)
                .execute();
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

    /**
     * 根据taskId获取businessKey
     *
     * @param taskId
     * @return
     */
    public String getBusinessKeyByTaskId(String taskId) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new Exception("未查询到对应的task信息！");
        }
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String business_key = pi.getBusinessKey();
        return business_key;
    }

    /**
     * 根据taskId获取processInstanceId
     */
    public String getProcessInstanceIdByTaskId(String taskId) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new Exception("未查询到对应的task信息！");
        }
        String processInstanceId = task.getProcessInstanceId();
        return processInstanceId;
    }

    /**
     * 根据taskId获取task
     */
    public Task getTaskByTaskId(String taskId) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new Exception("未查询到对应的task信息！");
        }
        return task;
    }

    /**
     * 给任务添加处理候选人
     *
     * @param taskId
     * @param assignees
     * @return
     */
    public String setCandidateUser(String taskId, List<String> assignees) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            assignees.stream().forEach(a -> {
                taskService.addCandidateUser(taskId, a);
            });
            return "";
        } else {
            return "";
        }
    }

    /**
     * 退回到起点
     */
    public void deny(String processInstanceId) {
        Task activeTask = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .singleResult();
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery()
                .taskId(activeTask.getId())
                .singleResult();
        // 获取流程定义
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(taskInstance.getProcessDefinitionId());
        // 获取当前活动
        ActivityImpl currentActivity = processDefinitionEntity.findActivity(taskInstance.getTaskDefinitionKey());
        // 获取起始活动
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .activityType("userTask")
                .processInstanceId(processInstanceId)
                .finished()
                .orderByHistoricActivityInstanceEndTime()
                .asc()
                .list();
        if (historicActivityInstances.size() == 0) {
            return;
        }
        ActivityImpl lastActivity = processDefinitionEntity.findActivity(historicActivityInstances.get(0).getActivityId());

        // 退回至起点
        runtimeService.createProcessInstanceModification(processInstanceId)
                .cancelAllForActivity(currentActivity.getActivityId())
                .startBeforeActivity(lastActivity.getActivityId())
                .setVariable("denyReason", "撤回")
                .execute();
    }

}
