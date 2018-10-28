package per.san.act;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipInputStream;

public class workflowTest {
    /**
     * 一条语句创建processEngine, 要求:
     * 1、配置文件必须在classpath根目录下
     * 2、配置文件名必须为activiti-context.xml或activiti.cfg.xml
     * 3、工厂对象的id必须为processEngine
     */
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    HistoryService historyService = processEngine.getHistoryService();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    TaskService taskService = processEngine.getTaskService();


    /**
     * 通过zipinputstream完成部署
     * 注意：这个的话，需要将bpmn和png文件进行压缩成zip文件，然后放在项目src目录下即可(当然其他目录也可以)
     */
    @Test
    public void testDeployFromZipInputStream() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("leave-01.zip");
        ZipInputStream zipInputStream = new ZipInputStream(in);
        repositoryService.createDeployment()
                .name("leave-process-01_name")
                .category("leave_category")
                .addZipInputStream(zipInputStream).deploy();
    }

    /**
     * 查看流程图
     * 根据deploymentId和name(在act_ge_bytearray数据表中)
     */
    @Test
    public void testShowImage() throws Exception {
        /**
         * deploymentID
         * 文件的名称和路径
         */
        InputStream inputStream = repositoryService
                .getResourceAsStream("2501", "leave-01.bpmn");
        OutputStream outputStream3 = new FileOutputStream("d:/leave-01.png");
        int b = -1;
        while ((b = inputStream.read()) != -1) {
            outputStream3.write(b);
        }
        inputStream.close();
        outputStream3.close();
    }

    /**
     * 根据pdid查看图片(在act_re_procdef数据表中)
     *
     * @throws Exception
     */
    @Test
    public void testShowImage2() throws Exception {
        InputStream inputStream = repositoryService.getProcessDiagram("leave_process:1:2504");
        OutputStream outputStream = new FileOutputStream("d:/leaveProcess.png");
        int b = -1;
        while ((b = inputStream.read()) != -1) {
            outputStream.write(b);
        }
        inputStream.close();
        outputStream.close();
    }

    /**
     * 部署流程定义
     */
    @Test
    public void deploy() {
        //获取仓库服务 ：管理流程定义
        Deployment deploy = repositoryService.createDeployment()
                //创建一个部署的构建器
                .addClasspathResource("leave-01.bpmn")
                //从类路径中添加资源,一次只能添加一个资源
                .name("请假审批流程333")//设置部署的名称

                .category("普通类别333")//设置部署的类别
                .deploy();
        System.out.println("部署的id" + deploy.getId());
        System.out.println("部署的名称" + deploy.getName());
    }

    /**
     * 创建流程实例
     */
    @Test
    public void startProcessInstance() {
        String leaveId = "333";
//        String staffId = "18239-1";
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("staffId", staffId);
        // 启动流程
        String processDefinitionId = "leave_process:3:162504";
        // 三个参数分别为(String processDefinitionKey, String businessKey, HashMap<String, Object> variables)
        ProcessInstance pi = runtimeService
                .startProcessInstanceById(processDefinitionId, leaveId);
        String processInstanceId = pi.getId();
        System.out.println("创建流程实例成功,流程实例id:" + processInstanceId + "  流程定义id:" + pi.getProcessDefinitionId());
        //默认完成第一步申请人的申请步骤
        Task task = taskService
                .createTaskQuery()
                .processInstanceId(pi.getId())
                .singleResult();
//        String nextStaffId = "04646";
//        map.put("staffId", nextStaffId);
        taskService.setAssignee(task.getId(), "18239-1-333");
        taskService.complete(task.getId());
    }

    /**
     * 执行任务 同意
     *
     * @return
     */
    @Test
    public void completeMyPersonTaskApproved() {
        String processInstanceId = "165001";
        Task task = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        String taskId = task.getId();
//        String nextStaffId = "02222";
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("staffId", nextStaffId);
        taskService.addComment(taskId, processInstanceId, "action", "APPROVED");
        taskService.setAssignee(taskId, "18239-4-333");
        taskService.complete(taskId);
        System.out.println("查看当前任务成功" + "完成任务，任务ID：" + taskId);
    }

    /**
     * 执行任务 拒绝
     *
     * @return
     */
    @Test
    public void completeMyPersonTaskRejected() {
        String processInstanceId = "147501";
        Task task = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        String taskId = task.getId();
//        String nextStaffId = "02222";
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("staffId", nextStaffId);
        taskService.addComment(taskId, processInstanceId, "action", "REJECTED");
        taskService.setAssignee(taskId, "18239-3-222");
        taskService.complete(taskId);
        turnTransition(processInstanceId, getApplicantActId(processInstanceId));
        System.out.println("查看当前任务成功" + "完成任务，任务ID：" + taskId);
    }

    /**
     * 终止流程实例
     *
     * @return
     */
    @Test
    public void deleteProcessInstance() {
        String processInstanceId = "165001";
        String reason = "reason：这个不符合规范，终止！333";
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
        runtimeService.deleteProcessInstance(processInstance.getId(), reason);
        System.out.println("停止taskId为：" + task.getId() + "的任务");
    }

    /**
     * 查询所有的正在执行的任务
     */
    @Test
    public void testQueryTask() {
        List<Task> tasks = taskService.createTaskQuery().list();
        for (Task task : tasks) {
            System.out.println(task.getName());
        }
    }

    /**
     * 根据piid得到当前正在执行的流程实例的正在活动的节点
     */
    @Test
    public void testActivity() {
        /**
         * 根据piid得到流程实例
         */
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId("7501")
                .singleResult();
        String activityId = pi.getActivityId();
        //当前流程实例正在执行的activityId
        System.out.println(activityId);
    }

    /**
     * 查找个人任务
     */
    @Test
    public void getMyTaskInfo() {
        String staffId = "18239-1";
        List<Task> listTask = taskService.createTaskQuery().taskAssignee(staffId).list();
        if (listTask != null && listTask.size() > 0) {
            for (Task task : listTask) {
                System.out.println("任务ID：" + task.getId());
                System.out.println("任务名称：" + task.getName());
                System.out.println("任务时间：" + task.getCreateTime());
                System.out.println("任务的办理人：" + task.getAssignee());
                System.out.println("任务的实例ID：" + task.getProcessDefinitionId());
                System.out.println("#########################################");
            }
        }
    }

    /**
     * 查询执行对象表,使用流程实例ID和当前活动的名称（receivetask1）
     */
    @Test
    public void queryExecution() {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("leave_process")
                .singleResult();
        //3.查询执行对象表,使用流程实例ID和当前活动的名称（receivetask1）
        String processInstanceId = pi.getId();//得到流程实例ID
        Execution execution1 = runtimeService
                .createExecutionQuery()
                .processInstanceId(processInstanceId)
                //流程实例ID//
                .activityId("receivetask1")//当前活动的名称
                .singleResult();
        //5.向后执行一步
        runtimeService
                .signalEventReceived(execution1.getId());
    }


    @Test
    public void queryProcessInstanceHistory() {
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .orderByHistoricActivityInstanceEndTime()
                .desc()
                .processInstanceId("7501")
                .list();
        for (HistoricActivityInstance h : list) {
            System.out.println(h.getId());
            System.out.println(h.getActivityId());
            System.out.println(h.getAssignee());
            System.out.println(h.getExecutionId());
            System.out.println(h.getProcessInstanceId());
            System.out.println("##############################");
        }
    }

    public String getApplicantActId(String procInstId) {
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(procInstId)
                .activityType("userTask")
                .orderByHistoricActivityInstanceEndTime()
                .asc().list();
        String proDefId = null;
        if (historicActivityInstances != null && historicActivityInstances.size() != 0) {
            proDefId = historicActivityInstances.get(0).getProcessDefinitionId();
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(proDefId);
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        Iterator var8 = flowElements.iterator();

        while (var8.hasNext()) {
            FlowElement flowElement = (FlowElement) var8.next();
            if (flowElement instanceof UserTask) {
                if ("Applicant".equals(flowElement.getDocumentation())) {
                    return flowElement.getId();

                }
            }
        }
        return null;
    }
    public void turnTransition(String procInstId, String targetActivityId) {
        Task currentTask = taskService.createTaskQuery()
                .processInstanceId(procInstId)
                .singleResult();
        // 获取当前节点Id
        String currentActivityId = currentTask.getTaskDefinitionKey();
        // 获取模型实体
        String processDefinitionId = currentTask.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        // 获取当前节点
        FlowElement currentFlow = bpmnModel.getFlowElement(currentActivityId);
        // 获取目标节点
        FlowElement targetFlow = bpmnModel.getFlowElement(targetActivityId);
        //创建连线
        String uuid = UUID.randomUUID().toString().replace("-", "");
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId(uuid);
        newSequenceFlow.setSourceFlowElement(currentFlow);
        newSequenceFlow.setTargetFlowElement(targetFlow);
        //设置条件
        newSequenceFlow.setConditionExpression("${\"+uuid+\"==\"" + uuid + "\"}");
        //添加连线至bpmn
        bpmnModel.getMainProcess().addFlowElement(newSequenceFlow);
        //添加变量（保证这根线独一无二）
        Map<String, Object> variables = new HashMap<>(1);
        variables.put(uuid, uuid);
        //提交
        taskService.addComment(currentTask.getId(), currentTask.getProcessInstanceId(), "跳转节点");
        //完成任务
        taskService.complete(currentTask.getId(), variables);
        //删除连线
        bpmnModel.getMainProcess().removeFlowElement(uuid);
    }
}
