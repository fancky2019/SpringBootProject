package com.example.demo.controller;

import com.example.demo.service.camunda.CamundaService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.Deployment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
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

    @GetMapping("/completedTask")
    public String completedTask(String taskId) {

        HashMap<String, Object> param = new HashMap<>();
        param.put("approve", true);
        camundaService.completedTask(taskId, param);
        return "completedTask";
    }


}
