import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
public class ActivitiTest {

    @Test
    public void testV1(){

        //创建流程引擎 processEngine, 会检查库表，如果没有就自动创建
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(defaultProcessEngine);

    }


    /**
     * 流程定义部署到数据库
     */
    @Test
    public void testDeploy(){

        //创建流程引擎 processEngine, 会检查库表，如果没有就自动创建
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //资源服务
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //部署流程定义
        Deployment deployed = repositoryService.createDeployment()
                .addClasspathResource("bpmn/fiona.bpmn20.xml")
                .deploy();

        System.out.println("id="+deployed.getId());
        System.out.println("name="+deployed.getName());
        System.out.println("key="+deployed.getKey());
        System.out.println("deploymentTime="+deployed.getDeploymentTime());

    }

    @Test
    public void testDeployV2(){

        //创建流程引擎 processEngine, 会检查库表，如果没有就自动创建
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //资源服务
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //部署流程定义
        Deployment deployed = repositoryService.createDeployment()
                .addClasspathResource("bpmn/testDay.bpmn20.xml")
                .name("请求流程")
                .key("testDayKey")
                .deploy();

        System.out.println("id="+deployed.getId());
        System.out.println("name="+deployed.getName());
        System.out.println("key="+deployed.getKey());
        System.out.println("deploymentTime="+deployed.getDeploymentTime());

    }


    /**
     * 发起流程实例
     */
    @Test
    public void testStartProcessInstance(){
        //获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RuntimeService runtimeService = processEngine.getRuntimeService();
        //根据流程定义的key启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testDay");
        System.out.println("流程定义id="+processInstance.getProcessDefinitionId());
        System.out.println("流程定义key="+processInstance.getProcessDefinitionKey());
        System.out.println("流程实例id="+processInstance.getId());
        System.out.println("实例名称name="+processInstance.getName());

    }


    /**
     * 查询任务
     */
    @Test
    public void testCompleteTask(){
        //获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService = processEngine.getTaskService();

        //查询代办任务
        List<Task> list = taskService.createTaskQuery()
                //.processDefinitionId("testDay:1:5001")
                .taskAssignee("王五")
                .list();

        //打印任务
        for (Task task : list) {
            System.out.println("任务id="+task.getId());
            System.out.println("任务负责人="+task.getAssignee());
            System.out.println("任务名称="+task.getName());
            System.out.println("任务所属流程实例id="+task.getProcessInstanceId());
            System.out.println("任务所属流程定义id="+task.getProcessDefinitionId());

            taskService.complete(task.getId());

        }


    }


    /**
     * 添加审批意见
     */
    @Test
    public void testAddComment(){
        //获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService = processEngine.getTaskService();

        //查询代办任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionId("testDay:1:5003")
                .list();

        //打印任务
        for (Task task : list) {
            System.out.println("任务id="+task.getId());
            System.out.println("任务负责人="+task.getAssignee());
            System.out.println("任务名称="+task.getName());
            System.out.println("任务所属流程实例id="+task.getProcessInstanceId());
            System.out.println("任务所属流程定义id="+task.getProcessDefinitionId());

            //taskService.complete(task.getId());
            //第一个参数是任务id，第二个参数是流程实例id，第三个参数是审批意见
            taskService.addComment(task.getId(),task.getProcessInstanceId(),"通过，但是不要太多次，注意身体,好好休息");
            taskService.complete(task.getId());
        }

    }


    /**
     * 查询历史任务信息
     */
    @Test
    public void testHistoryTask(){
        //获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        HistoryService historyService = processEngine.getHistoryService();

        //查询ACT_HI_TASKINST表
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                //.processDefinitionKey("")
                //.processDefinitionId()
                .list();
        System.out.println("数量:"+list.size());
        for (HistoricTaskInstance task : list){
            System.out.println("任务id="+task.getId());
            System.out.println("任务负责人="+task.getAssignee());
            System.out.println("任务名称="+task.getName());
        }

    }


    /**
     * 查询历史流程实例信息
     */
    @Test
    public void testHistoryProcessInstance(){
        //获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        //查找对应的表 ACT_HI_ACTINST
        HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery();

        //拼接条件
        List<HistoricActivityInstance> list = query.processDefinitionId("uel:1:25003").list();
        System.out.println("数量:"+list.size());
        for (HistoricActivityInstance activityInstance : list){
            System.out.println("任务id="+activityInstance.getId());
            System.out.println("任务负责人="+activityInstance.getAssignee());
            System.out.println("任务名称="+activityInstance.getActivityName());
        }

    }


}