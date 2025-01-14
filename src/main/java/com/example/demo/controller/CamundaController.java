package com.example.demo.controller;

import com.example.demo.service.camunda.CamundaService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.Deployment;

import org.camunda.bpm.engine.task.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/camunda")
public class CamundaController {
    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private IdentityService identityService;

    @Resource
    private TaskService taskService;


    CamundaService camundaService;

    public CamundaController(CamundaService camundaService) {
        this.camundaService = camundaService;
    }

    @GetMapping("/deploy")
    public String deploy(String deploymentName, String bpmnFileName) {

        camundaService.deploy(deploymentName, bpmnFileName);
        return "deploy";
    }

    /**
     *
     * @param processId process id
     * @param businessKey
     * @return
     */
    @GetMapping("/startProcess")
    public String startProcess(String processId, String businessKey) {

        HashMap<String, Object> param = new HashMap<>();
        param.put("starter", "fancky");
        String taskId = camundaService.startProcess(processId, businessKey, param, "fanc");
        return "startProcess:" + taskId;
    }

    /**
     * 查询任务
     *    待办
     *    流程定义ID:processDefinitionId : 我们部署流程时产生的ID，每个流程都会产生一个流程定义ID
     *    流程实例ID:processInstanceId : 我们启动流程实例的时候，产生的流程实例ID
     * @return
     */
    @GetMapping("/query")
    public void queryTask(){
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee("demo")
                //.processInstanceId("0068591b-a979-11ef-a6bc-046c592bc290")
                .list();//查询所有待办任务
        if(list != null && list.size() > 0){
            for(Task task : list){
                System.out.println("task.getId() = " + task.getId());
                System.out.println("task.getAssignee() = " + task.getAssignee());
            }
        }
    }



    @GetMapping("/completedTask")
    public String completedTask(String taskId,Boolean approve) throws Exception {

        HashMap<String, Object> param = new HashMap<>();
        param.put("approve", approve);
        camundaService.completedTask(taskId, param);
        return "completedTask";
    }


}
