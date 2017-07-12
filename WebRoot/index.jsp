<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<base href="<%=basePath%>">

		<title>点餐系统后台</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link rel="stylesheet" href="style/PublicStyle.css" type="text/css"></link>
	    <script type="text/javascript" src="/WorkDiaryService/Script/jquery-1.4.4.js"></script>
		<script type="text/javascript">
function fndelete(id) {

	if (confirm("确定删除当前记录吗？")) {
		$.get("/MealAppService/servlet/ServletService?Ran=" + Math.random(), {
			Action : "Del",
			ID : id,
			Table : "dishes"
		}, function(data) {
			if (data == 1) {
				alert("恭喜您,删除成功！");
				location.href = location.href;
			} else {
				alert("对不起,删除失败,请稍后再试！");
			}

		});
	}
}
function search() {
	if ($("#txtkeyword").val() == "") {
		location.href = "/MealAppService/servlet/GridServlet?Action=getlist&currentpage=1";
	} else {
		location.href = location.href + "&msg="
				+ encodeURI($("#txtkeyword").val());
	}
}
</script>

	</head>
	<body>
		<div class="backbody" style="background: #FFF;">
			<jsp:include page="top.jsp"></jsp:include>
			<div style=" padding:10px;">
				<div style="padding: 15px 5px 10px 0;">
					<table class="SearchTable" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td>
								标题：
							</td>
							<td>
								<input type="text" class="textbox" id="txtkeyword" />
							</td>
							<td>
								<input type="button" class="btnClass_79px_A" value="搜索"
									onclick="javascript:search();" />
							</td>
							<td>
								<input type="button" class="btnClass_79px_A" value="添加"
									onclick="javascript:location.href='/MealAppService/uploadFileDialog.jsp'" />

							</td>
						</tr>
					</table>
					<table width="940px;" class="GridTable" border="0" cellspacing="0"
						cellpadding="0" id="GridTableID">

						<tr>
							<th>
								图片
							</th>
							<th>
								标题
							</th>
							<th style='width:300px;'>
								简介
							</th>
							<th>
								类型
							</th>
							<th>
								单价
							</th>
							<th>
								剩余数量
							</th>
							<th style='width:60px;'>
								修改
							</th>
							<th class="right" style='width:60px;'>
								删除
							</th>
						</tr>
						<c:forEach var="data" items="${datalist}">
							<tr>
								<td>
									<a target="_blank" href="UploadFile/${data.img_url}"><img src="UploadFile/${data.img_url}" width="60px"
										height="30px" /></a>
								</td>
								
								<td>
									${data.title}
								</td>
								<td >
									${data.intro}
								</td>
								<td>
									${data.typename}
								</td>
							    <td >
									${data.price}
								</td>
                                <td >
									${data.amount}
								</td>
							       
								<td>
									 
									<input id='Button2' title="修改" type="button" value=""
										class="btnGrid"
										onclick="javascript:location.href='/MealAppService/uploadFileDialog.jsp?id=${data.id}'"
										style="background: url(images/edit.gif)" />
								
								</td>
								<td class="right">
									<input id='${data.id}'
										onclick='javascript:fndelete(${data.id})' title="删除"
										type="button" value="" class="btnDel  btnGrid"
										style="background: url(images/delete.gif)" />
								</td>
							</tr>
						</c:forEach>
					</table>
					<div style="margin-top: 5px;">
						<a href="servlet/GridServlet?Action=getlist&currentpage=${1}">首页</a>
						<a
							href="servlet/GridServlet?Action=getlist&currentpage=${currentpage-1}">上一页</a>
						<a
							href="servlet/GridServlet?Action=getlist&currentpage=${currentpage+1}">下一页</a>
						<a
							href="servlet/GridServlet?Action=getlist&currentpage=${pagecount}">尾页</a>
						当前页是第${currentpage}/${pagecount}页,共有${total}条记录
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
