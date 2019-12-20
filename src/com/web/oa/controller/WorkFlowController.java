package com.web.oa.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.Leavebill;
import com.web.oa.service.BaoxiaoService;
import com.web.oa.service.LeaveService;
import com.web.oa.service.WorkFlowService;
import com.web.oa.utils.Constants;

@Controller
public class WorkFlowController {
	
	@Autowired
	private WorkFlowService workFlowService;
	
	@Autowired
	private LeaveService leaveService;
	
	@Autowired
	private BaoxiaoService baoxiaoService;
	
	//部署流程图
	@RequestMapping("/deployProcess")
	public String deployProcess(String processName,MultipartFile fileName) {
		try {
			workFlowService.saveDeployProcess(processName, fileName.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/processDefinitionList";
		
	}
	//查看流程
	@RequestMapping("/processDefinitionList")
	public ModelAndView processDefinitionList() {
		List<Deployment> depList = workFlowService.findDeploymentList();
		List<ProcessDefinition> pdList = workFlowService.findProcessDefList();
		ModelAndView mv = new  ModelAndView();
		mv.addObject("depList", depList);
		mv.addObject("pdList", pdList);
		mv.setViewName("workflow_list");
		return mv;
		
	}

	/*//提交请假单
	 * @RequestMapping("/saveStartLeave") 
	 * public String saveStartLeave(Leavebill bill,HttpSession session) { 
	 * Employee employee = (Employee)
	 * session.getAttribute(Constants.GLOBAL_SESSION_ID);
	 * this.workFlowService.saveStartLeavebillProcess(bill,employee); return
	 * "redirect:/myTaskList"; }
	 */
	//提交报销单
	@RequestMapping("/saveStartBaoxiao")
	public String saveStartBaoxiao(Baoxiaobill bill,HttpSession session) {
		Employee employee = new Employee();
		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		employee.setId(activeUser.getUserid());
		employee.setName(activeUser.getUsercode());
		this.workFlowService.saveStartBaoxiaobillProcess(bill,employee);
		return "redirect:/myTaskList";
	}
	//查看我的任务
	@RequestMapping("/myTaskList")
	public ModelAndView myTaskList(HttpSession session) {
		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		List<Task> list = this.workFlowService.findMyTaskList(activeUser.getUsercode());
		ModelAndView mv = new  ModelAndView();
		mv.addObject("taskList", list);;
		mv.setViewName("workflow_task");
		return mv;
	}
	//审批当前任务
	@RequestMapping("/viewTaskFrom")
	public ModelAndView viewTaskFrom(String taskId) {
		//1.根据任务的Id查询当前的请假的业务对象
		//Leavebill bill = this.workFlowService.findLeaveBillByTaskId(taskId);
		Baoxiaobill bill = this.workFlowService.findBaoxiaoBillByTaskId(taskId);
		//2.根据任务查询批注信息
		List<Comment> commentList= this.workFlowService.findCommentListByTask(taskId);
		////获取连线的名称集合
		List<String> outComeList = this.workFlowService.findOutComeListByTaskId(taskId);
		ModelAndView mv = new ModelAndView();
		mv.addObject("bill", bill);
		mv.addObject("outComeList",outComeList);
		mv.addObject("commentList", commentList);
		mv.addObject("taskId", taskId);
		//mv.setViewName("approve_leave");
		mv.setViewName("approve_baoxiao");
		return mv;
	}
	//流程推进
	@RequestMapping("/submitTask")
	public String submitTask(String taskId,Long id,String comment,HttpSession session,String outComeName) {
		//1.获取emp
		//Employee emp = (Employee) session.getAttribute(Constants.GLOBAL_SESSION_ID);
		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		//2.通过emp的getName(),推进流程
		this.workFlowService.saveSubmitTask(id,taskId,comment,activeUser.getUsercode(),outComeName);
		//3.重定向返回
		return "redirect:/myTaskList";
		
	}
	//查看流程图
	@RequestMapping("/viewImage")
	public void viewImage(String deploymentId,String imageName,HttpServletResponse response) throws IOException {
		InputStream in =this.workFlowService.findImageInputStream(deploymentId,imageName);
		OutputStream out =response.getOutputStream();
		
		for(int b=-1;(b=in.read())!=-1;) {
			out.write(b);
		}
		out.close();
		in.close();
	}
	//查看当前的流程图
	@RequestMapping("/viewCurrentImage")
	public ModelAndView viewCurrentImage(String taskId) {
		/**一：查看流程图*/
		//1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
		ProcessDefinition pd = workFlowService.findProcessDefinitionByTaskId(taskId);
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("deploymentId", pd.getDeploymentId());
		mv.addObject("imageName", pd.getDiagramResourceName());
		/**二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中*/
		Map<String, Object> map = workFlowService.findCoordingByTask(taskId);

		mv.addObject("acs", map);
		mv.setViewName("viewimage");
		return mv;
		
	}
	//查看请假记录
	@RequestMapping("/myLeaveBill")
	public ModelAndView myLeaveBill(HttpSession session) {
		//Employee emp = (Employee) session.getAttribute(Constants.GLOBAL_SESSION_ID);
		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		ModelAndView mv = new ModelAndView();
		List<Leavebill> billList = this.leaveService.findLeaveBillByUserId(activeUser.getUserid());
		return null;
	}
	
	@RequestMapping("/delDeployment")
	public String delDeployment(String deploymentId) {
		this.workFlowService.removeProcessDefinitions(deploymentId);
		return "redirect:/processDefinitionList";
		
	}
	@RequestMapping("/viewHisComment")
	public ModelAndView viewHisComment(String id) {
		//1.获取Leavebill
		//Leavebill bill = this.leaveService.findLeaveBillByBillId(id);
		Baoxiaobill bill = this.baoxiaoService.findBaoxiaoBillByBillId(id);
		//2.获取List<Comment>
		List<Comment> commentList = this.workFlowService.findCommentListByBillId(id);
		ModelAndView mv =new ModelAndView();
		mv.addObject("bill", bill);
		mv.addObject("commentList",commentList);
		mv.setViewName("viewhis_mybaoxiao");
		return mv;
		
	}
	@RequestMapping("/viewCurrentImageByBill")
	public ModelAndView viewCurrentImageByBill(String billId) {
		/**一：查看流程图*/
		String bUSSINESS_KEY = Constants.BAOXIAOBILL_KEY+"."+billId;
		Task task = this.workFlowService.findTaskByBussinessKey(bUSSINESS_KEY);
		//1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
		ProcessDefinition pd = workFlowService.findProcessDefinitionByTaskId(task.getId());
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("deploymentId", pd.getDeploymentId());
		mv.addObject("imageName", pd.getDiagramResourceName());
		/**二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中*/
		Map<String, Object> map = workFlowService.findCoordingByTask(task.getId());

		mv.addObject("acs", map);
		mv.setViewName("viewimage");
		return mv;
		
	}
	
	
}
