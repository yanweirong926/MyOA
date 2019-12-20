package com.web.oa.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.oa.mapper.BaoxiaobillMapper;
import com.web.oa.mapper.LeavebillMapper;
import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.pojo.BaoxiaobillExample;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.Leavebill;
import com.web.oa.pojo.LeavebillExample;
import com.web.oa.service.WorkFlowService;
import com.web.oa.utils.Constants;

@Service("workFlowService")
public class WorkFlowServiceImpl implements WorkFlowService {
	
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private HistoryService historyService;
	
	@Autowired
	private LeavebillMapper leavebillMapper;
	
	@Autowired
	private BaoxiaobillMapper baoxiaobillMapper;
	
	@Override
	public void saveDeployProcess(String processName, InputStream input) {
		ZipInputStream zipInput = new ZipInputStream(input);
		Deployment deployment = this.repositoryService
				.createDeployment()
				.name(processName)
				.addZipInputStream(zipInput)
				.deploy();
		
	}
	@Override
	public List<Deployment> findDeploymentList() {
		List<Deployment> list = this.repositoryService.createDeploymentQuery().list();
		return list;
	}
	@Override
	public List<ProcessDefinition> findProcessDefList() {
		List<ProcessDefinition> list =this.repositoryService
											.createProcessDefinitionQuery()
											.list();
		return list;
	}
	@Override
	public List<Task> findMyTaskList(String name) {
		List<Task> list =  this.taskService
							.createTaskQuery()
							.taskAssignee(name)
							.list();
		
		return list;
	}
	@Override
	public Leavebill findLeaveBillByTaskId(String taskId) {
		Task task = findTaskByTaskId(taskId);
		ProcessInstance pi= this.runtimeService.createProcessInstanceQuery()
								.processInstanceId(task.getProcessInstanceId()).singleResult();
		String bussiness_key= pi.getBusinessKey();
		System.out.println(bussiness_key);
		
		String billId =  bussiness_key.split("\\.")[1];
		Leavebill bill= leavebillMapper.selectByPrimaryKey(Long.parseLong(billId));
		return bill;
	}
	@Override
	public List<Comment> findCommentListByTask(String taskId) {
		Task task = findTaskByTaskId(taskId);
		List<Comment> commentlist= this.taskService.getProcessInstanceComments(task.getProcessInstanceId());
		return commentlist;
	}
	private Task findTaskByTaskId(String taskId) {
		Task task= this.taskService.createTaskQuery().taskId(taskId).singleResult();
		return task;
	}
	@Override
	public void saveSubmitTask(Long id, String taskId, String comment, String name,String outComeName) {
		Task task = findTaskByTaskId(taskId);
		String processInstanceId = task.getProcessInstanceId();
		//加当前任务的审核人
		Authentication.setAuthenticatedUserId(name);
		this.taskService.addComment(taskId, processInstanceId, comment);
		//查询报销单信息
		Baoxiaobill bill = this.baoxiaobillMapper.selectByPrimaryKey(id);
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("message", outComeName);
		
		//2.完成任务（推进）
		this.taskService.complete(taskId,map);
		
		//3.判断当前流程实例是否结束，如果结束修改请假单的状态
		ProcessInstance pi = this.runtimeService.createProcessInstanceQuery()
										.processInstanceId(processInstanceId).singleResult();
		if(pi==null) {
			//Leavebill bill = this.leavebillMapper.selectByPrimaryKey(id);
			
			bill.setState(2);
			//this.leavebillMapper.updateByPrimaryKey(bill);
			this.baoxiaobillMapper.updateByPrimaryKey(bill);
		}
	}
	@Override
	public InputStream findImageInputStream(String deploymentId, String imageName) {
		InputStream in = this.repositoryService.getResourceAsStream(deploymentId, imageName);
		return in;
	}
	@Override
	public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
		//使用任务ID，查询任务对象
		Task task = findTaskByTaskId(taskId);
		//获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		//查询流程定义的对象
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()//创建流程定义查询对象，对应表act_re_procdef 
					.processDefinitionId(processDefinitionId)//使用流程定义ID查询
					.singleResult();
		return pd;
	}

	@Override
	public Map<String, Object> findCoordingByTask(String taskId) {
		//存放坐标
		Map<String, Object> map = new HashMap<String,Object>();
		//使用任务ID，查询任务对象
		Task task = findTaskByTaskId(taskId);
		//获取流程定义的ID
		String processDefinitionId = task.getProcessDefinitionId();
		//获取流程定义的实体对象（对应.bpmn文件中的数据）
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
		//流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//创建流程实例查询
											.processInstanceId(processInstanceId)//使用流程实例ID查询
											.singleResult();
		//获取当前活动的ID
		String activityId = pi.getActivityId();
		//获取当前活动对象
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);//活动ID
		//获取坐标
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("width", activityImpl.getWidth());
		map.put("height", activityImpl.getHeight());
		return map;
	}
	
	@Override
	public void removeProcessDefinitions(String deploymentId) {
		this.repositoryService.deleteDeployment(deploymentId, true);
		
	}
	

	@Override
	public void saveStartLeavebillProcess(Leavebill bill, Employee emp) {
		//1.保存申请单到DB
		bill.setLeavedate(new Date());
		bill.setState(1);
		bill.setUserId(emp.getId());
		leavebillMapper.insert(bill);
				
		System.out.println(bill.getId());
		//2.启动流程实例
		String key = Constants.LEAVEBILL_KEY;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constants.USER_ID, emp.getName());
		String BUSINESS_KEY = Constants.LEAVEBILL_KEY+"."+bill.getId();
		this.runtimeService.startProcessInstanceByKey(key , BUSINESS_KEY, map );
		
	}
	@Override
	public void saveStartBaoxiaobillProcess(Baoxiaobill bill, Employee emp) {
		//1.保存申请单到DB
		bill.setCreatdate(new Date());;
		bill.setState(1);
		bill.setUserId(emp.getId());
		baoxiaobillMapper.insert(bill);
				
		System.out.println(bill.getId());
		//2.启动流程实例
		String key = Constants.BAOXIAOBILL_KEY;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constants.USER_ID, emp.getName());
		String BUSINESS_KEY = Constants.BAOXIAOBILL_KEY+"."+bill.getId();
		this.runtimeService.startProcessInstanceByKey(key , BUSINESS_KEY, map );
		
	}
	@Override
	public Baoxiaobill findBaoxiaoBillByTaskId(String taskId) {
		Task task = findTaskByTaskId(taskId);
		ProcessInstance pi= this.runtimeService.createProcessInstanceQuery()
								.processInstanceId(task.getProcessInstanceId()).singleResult();
		String bussiness_key= pi.getBusinessKey();
		System.out.println(bussiness_key);
		
		String billId =  bussiness_key.split("\\.")[1];
		Baoxiaobill bill= baoxiaobillMapper.selectByPrimaryKey(Long.parseLong(billId));
		return bill;
	}
	@Override
	public List<String> findOutComeListByTaskId(String taskId) {
		//返回存放连线的名称集合
		List<String> list = new ArrayList<String>();
		//1:使用任务ID，查询任务对象
		Task task = findTaskByTaskId(taskId);
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
		//查询报销单信息
		Baoxiaobill baoxiaobill = this.findBaoxiaoBillByTaskId(taskId);
		//5：获取当前活动完成之后连线的名称
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		if(pvmList!=null && pvmList.size()>0){
			for(PvmTransition pvm:pvmList){
				String name = (String) pvm.getProperty("name");
				if(StringUtils.isNotBlank(name)){
					//判断name是否为‘金额小于5000’，‘金额大于5000’
						//判断报销金额是否大于5000
						if(name.equals(Constants.OUTCOME_NAME_M)&&baoxiaobill.getMoney().intValue()>5000) {
							list.add(Constants.OUTCOME_NAME_M);
						}else if(name.equals(Constants.OUTCOME_NAME_F)&&baoxiaobill.getMoney().intValue()<=5000) {
							list.add(Constants.OUTCOME_NAME_F);
						}else if(!name.equals(Constants.OUTCOME_NAME_M)&&!name.equals(Constants.OUTCOME_NAME_F)) {
							list.add(name);
						}
						
					
					
				}else{
					list.add(Constants.MESSAGE_COMM);
				}
			}
		}
		return list;
	}
	//根据业务表的数据关联流程任务
	@Override
	public Task findTaskByBussinessKey(String bUSSINESS_KEY) {
		Task task = this.taskService.createTaskQuery().processInstanceBusinessKey(bUSSINESS_KEY).singleResult();
		return task;
	}

	//根据报销单ID查询历史批注
	@Override
	public List<Comment> findCommentByBaoxiaoBillId(long id) {
		String bussiness_key = Constants.BAOXIAOBILL_KEY +"."+id;
		HistoricProcessInstance pi = this.historyService.createHistoricProcessInstanceQuery()
													.processInstanceBusinessKey(bussiness_key).singleResult();	
		List<Comment> commentList = this.taskService.getProcessInstanceComments(pi.getId());
		
		return commentList;
	}
	@Override
	public List<Comment> findCommentListByBillId(String id) {
		String bussiness_key = Constants.BAOXIAOBILL_KEY+"."+id;
		HistoricProcessInstance pi =  this.historyService.createHistoricProcessInstanceQuery()
												.processInstanceBusinessKey(bussiness_key).singleResult();
		List<Comment> list = this.taskService.getProcessInstanceComments(pi.getId());
		return list;
	}
	
	
	
	
}
