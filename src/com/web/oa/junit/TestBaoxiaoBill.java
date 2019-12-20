package com.web.oa.junit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import com.web.oa.mapper.BaoxiaobillMapper;
import com.web.oa.service.WorkFlowService;
import com.web.oa.utils.Constants;

/**
 * act_re_deployment  流程部署信息表
 * act_re_procdef     流程定义表
 *                    流程定义相当于一个“模板”
 *                    流程实例表示每次某个审批流
 * act_ge_bytearray   流程资源表： xxx.bpmn  xxx.png
 * act_ru_execution   流程执行表（操作表）
 * act_hi_procinst    流程实例历史表
 * act_ru_task        任务实例表
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/applicationContext.xml","classpath:spring/springmvc.xml"})
public class TestBaoxiaoBill {

	//ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();  // 默认获取activiti.cfg.xml
	
	@Autowired
	private BaoxiaobillMapper baoxiaobillMapper;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private FormService formService;
	@Autowired
	private HistoryService historyService;
	
	@Autowired
	private WorkFlowService workFlowService;
	
	@Test   //1 部署流程-->DB
	public void testDeployDB1() throws FileNotFoundException {
		
		InputStream inputstream = new FileInputStream("D:/diagram/baoxiaoprocess.zip");
		ZipInputStream zipInputstream = new ZipInputStream(inputstream );
		Deployment deployment = repositoryService.createDeployment()
								.name("报销测试007")
								.addZipInputStream(zipInputstream )
								.deploy();
		System.out.println("deployment id: " + deployment.getId());
		System.out.println("deployment name: " + deployment.getName());
	}
	
	@Test//2.启动流程实例
	public void testStartProcess() {
		//使用当前对象获取到流程定义的key（对象的名称就是流程定义的key）
		String key= Constants.BAOXIAOBILL_KEY;
		String baoxiaoId = "";
		
		Map<String, Object> variables = new HashMap<String,Object>();
		variables.put("inputUser", "zhang");//表示惟一用户

		//格式：baoxiao.id的形式（使用流程变量）
		String objId = key+"."+baoxiaoId;
		variables.put("objId", objId);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key,objId, variables);
		System.out.println("流程实例ID:" + pi.getId());
		System.out.println("流程定义ID:" + pi.getProcessDefinitionId());		
	}
	
	@Test//3.查询当前任务人的待办事务
	public void testFindTaskByAssignee(){
		String name = "jack";
		List<Task> list = taskService.createTaskQuery().taskAssignee(name ).list();
		for (Task task : list) {
			System.out.println("任务id:" + task.getId());
			System.out.println("任务所属的流程实例id:" + task.getProcessInstanceId());
			System.out.println("任务创建的时间： " + task.getCreateTime());
		}
	}
	
	@Test//4. 流程的推进
	public void testTaskFinish() {
		String taskId = "6405";
		taskService.complete(taskId );
		System.out.println("任务已经完成");
	}
	
	
	@Test//6. 查看流程定义图
	public void viewPic() throws IOException {
		String deploymentId = "6301";
		String resourceName = "baoxiaoprocess.png";
		InputStream in = repositoryService
									.getResourceAsStream(deploymentId , resourceName );
		File targetFile = new File("D:/diagram/"+resourceName);
		//FileUtils.copyInputStreamToFile(in, targetFile );		
		//3：从response对象获取输出流
		OutputStream out = new FileOutputStream(targetFile);
		//4：将输入流中的数据读取出来，写到输出流中
		for(int b=-1;(b=in.read())!=-1;){
			out.write(b);
		}
		out.close();
		in.close();
		System.out.println("图片已经保存");
	}
	
	@Test
	public void viewCurrentImage(){
		String taskId = "6610";
		/**一：查看流程图*/
		//1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
		ProcessDefinition pd = workFlowService.findProcessDefinitionByTaskId(taskId);

		System.out.println("deploymentId: " + pd.getDeploymentId());
		System.out.println("imageName: "+pd.getDiagramResourceName());
		/**二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中*/
		Map<String, Object> map = workFlowService.findCoordingByTask(taskId);

		System.out.println("x: " + map.get("x"));
		System.out.println("y: " + map.get("y"));
		System.out.println("width: " + map.get("width"));
		System.out.println("height: " + map.get("height"));
	}
	
	@Test
	public void viewCurrentImageByBill() {
		// 业务表的id ---> 流程定义
		String baoxiaoId = "13";
		String bussinessKey = Constants.BAOXIAOBILL_KEY + "." + baoxiaoId; // baoxiao.13
		
		Task task = taskService.createTaskQuery().processInstanceBusinessKey(bussinessKey).singleResult();
		
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
								.processDefinitionId(task.getProcessDefinitionId()).singleResult();
		

		System.out.println("deploymentId: " + pd.getDeploymentId());
		System.out.println("imageName: "+pd.getDiagramResourceName());
		/**二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中*/
		Map<String, Object> map = workFlowService.findCoordingByTask(task.getId());

		System.out.println("x: " + map.get("x"));
		System.out.println("y: " + map.get("y"));
		System.out.println("width: " + map.get("width"));
		System.out.println("height: " + map.get("height"));
	}
	
	
	@Test
	public void testFindOutComeListByTaskId() {
		//返回存放连线的名称集合
		List<String> list = new ArrayList<String>();
		String taskId = "6502";
		//1:使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery().taskId(taskId ).singleResult();
		//2：获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		//3：查询ProcessDefinitionEntiy对象
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
		//使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
					.processInstanceId(processInstanceId)//使用流程实例ID查询
					.singleResult();
		//获取当前活动的id
		String activityId = pi.getActivityId();
		//4：获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		//5：获取当前活动完成之后连线的名称
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		if(pvmList!=null && pvmList.size()>0){
			for(PvmTransition pvm:pvmList){
				String name = (String) pvm.getProperty("name");
				if(StringUtils.isNotBlank(name)){
					list.add(name);
				} else{
					list.add("默认提交");
				}
			}
		}
		
		for (String name : list) {
			System.out.println(name);
		}
		
	}

	
}
