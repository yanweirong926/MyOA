package com.web.oa.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.Leavebill;

public interface WorkFlowService {
	
	public void saveDeployProcess(String processName,InputStream input);
	
	public List<Deployment> findDeploymentList();
	
	public List<ProcessDefinition> findProcessDefList();

	public void saveStartLeavebillProcess(Leavebill bill, Employee emp);
	
	public void saveStartBaoxiaobillProcess(Baoxiaobill bill, Employee emp);

	public List<Task> findMyTaskList(String name);

	public Leavebill findLeaveBillByTaskId(String taskId);
	
	public Baoxiaobill findBaoxiaoBillByTaskId(String taskId);

	public List<Comment> findCommentListByTask(String taskId);

	public void saveSubmitTask(Long id, String taskId, String comment, String name,String outComeName);

	public InputStream findImageInputStream(String deploymentId, String imageName);

	public ProcessDefinition findProcessDefinitionByTaskId(String taskId);

	public Map<String, Object> findCoordingByTask(String taskId);

	public void removeProcessDefinitions(String deploymentId);

	public List<Comment> findCommentListByBillId(String id);
	
	//获取连线名称
	public List<String> findOutComeListByTaskId(String taskId); 
	
	public Task findTaskByBussinessKey(String bUSSINESS_KEY);
	
	public List<Comment> findCommentByBaoxiaoBillId(long id);
}
