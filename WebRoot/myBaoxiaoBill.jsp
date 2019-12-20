<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>我的报销单</title>

    <!-- Bootstrap -->
    <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="css/content.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <script src="js/jquery.min.js"></script>
    <script src="bootstrap/js/bootstrap.min.js"></script>
    
</head>
<body>

<!--路径导航-->
<ol class="breadcrumb breadcrumb_nav">
    <li>首页</li>
    <li>报销管理</li>
    <li class="active">我的报销单</li>
</ol>
<!--路径导航-->

<div class="page-content">
    <form class="form-inline">
        <div class="panel panel-default">
            <div class="panel-heading">我的报销单列表</div>
            
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead>
                    <tr>
                        <th width="5%">报销单ID</th>
                        <th width="5%">报销金额</th>
                        <th width="10%">标题</th>
                        <th width="25%">备注</th>
                        <th width="20%">时间</th>
                        <th width="10%">状态</th>
                        <th width="25%">操作</th>
                    </tr>
                    </thead>
                    <tbody id="tb" >
						<c:forEach var="bill" items="${info.list }">
							<tr>
								<td>${bill.id}</td>
								<td>${bill.money}</td>
								<td>${bill.title}</td>
								<td>${bill.remark}</td>
								<td>
									<fmt:formatDate value="${bill.creatdate}" pattern="yyyy-MM-dd HH:mm:ss"/>
								</td>
								<c:choose>
									<c:when test="${bill.state==1}">
										<td>审核中</td>
										<td>
											<a href="${pageContext.request.contextPath }/viewHisComment?id=${bill.id}" class="btn btn-success btn-xs"><span class="glyphicon glyphicon-eye-open"></span> 查看审核记录</a>
											<a href="${pageContext.request.contextPath }/viewCurrentImageByBill?billId=${bill.id}" target="_blank" class="btn btn-success btn-xs"><span class="glyphicon glyphicon-eye-open"></span> 查看当前流程图</a>
										</td>
									</c:when>
									<c:otherwise>
										<td>审核完成</td>
										<td>
											<a href="${pageContext.request.contextPath }/delBaoxiaoBill?id=${bill.id}" class="btn btn-danger btn-xs"><span class="glyphicon glyphicon-remove"></span> 删除</a>
											<a href="${pageContext.request.contextPath }/viewHisComment?id=${bill.id}" class="btn btn-success btn-xs"><span class="glyphicon glyphicon-eye-open"></span> 查看审核记录</a>
										</td>
									</c:otherwise>
								</c:choose>	
							</tr>
						</c:forEach>
                    </tbody>
                    
                </table>
                <nav aria-label="Page navigation">
  									<ul class="pagination">
  										<li>
										  <a href="${pageContext.request.contextPath }/myBaoxiaoBill?pageNum=1" aria-label="Previous">
										      	首页
										   </a>
										</li>
										<c:choose>
											<c:when test="${info.pageNum>1 }">
												<li><a href="${pageContext.request.contextPath }/myBaoxiaoBill?pageNum=${info.pageNum-1 }">上页</a></li>
											</c:when>
											<c:otherwise>
												<li><a href="${pageContext.request.contextPath }/myBaoxiaoBill?pageNum=1">上页</a></li>
											</c:otherwise>
										</c:choose>
    									<c:choose>
											<c:when test="${info.pageNum<info.pages }">
												<li><a href="${pageContext.request.contextPath }/myBaoxiaoBill?pageNum=${info.pageNum+1 }">下页</a></li>
											</c:when>
											<c:otherwise>
												<li><a href="${pageContext.request.contextPath }/myBaoxiaoBill?pageNum=${info.pages }">下页</a></li>
											</c:otherwise>
										</c:choose>
    									<li>
										  <a href="${pageContext.request.contextPath }/myBaoxiaoBill?pageNum=${info.pages }" aria-label="Next">
										     	末页
										   </a>
										</li>
  									</ul>
								</nav>
            </div>
        </div>
    </form>
    
</div>
</body>
</html>