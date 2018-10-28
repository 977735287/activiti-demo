package per.san.act;

import org.activiti.engine.*;

public class DeploymentTest {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    HistoryService historyService = processEngine.getHistoryService();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    TaskService taskService = processEngine.getTaskService();


}
