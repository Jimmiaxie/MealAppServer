<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	HttpSession session11 = request.getSession(true);
	session11.removeAttribute("LoginID");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>登录</title>
<!-- 清除浏览器中的缓存，它和其它几句合起来用，就可以使你再次进入曾经访问过的页面时，ie浏览器必须从服务端下载最新的内容，达到刷新的效果 -->
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<!-- 搜索关键字  就是用搜索引擎搜索的时候 会和这个关键字匹配 从而找到你的网站 -->
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<!-- rel是关联的意思，关联的是一个样式表(stylesheet)文档，它表示这个link在文档初始化时将被使用 -->
<link rel="stylesheet" href="style/Style_Login.css" type="text/css"></link>
<link rel="stylesheet" href="style/PublicStyle.css" type="text/css"></link>
<script type="text/javascript" src="Script/jquery-1.4.4.js"></script><!-- 引用一个有jq的文件 -->
<Script type="text/javascript">
	$(function() {
		$("#button1")
				.click(
						function() {
							if ($("#txtLoginID").val() == "") {
								alert("请输入登录名");
								return;
							}
							if ($("#txtPassWord").val() == "") {
								alert("请输入密码");
								return;
							}

							var param = {
								Action : "adminlogin",
								loginid : $("#txtLoginID").val(),
								passwords : $("#txtPassWord").val()
								
							};

							$.get(
											"servlet/ServletService?ran="
													+ Math.random(),
											param,
											function(data) {
												if (data == 1) {
													location.href = "/MealAppService/servlet/GridServlet?Action=getlist&currentpage=0";
												} else {
													alert("登录失败");
												}
											});
						});
	})
</Script>
</head>

<body
	><!-- background-repeat设置或检索对象的背景图像为不平铺, cellspacing表示单元间隔 -->
	<div style="background: url(images/b_login_bg.jpg) center; background-repeat: no-repeat; width: 1000px; height: 600px; margin: 0 auto; position: absolute; left:expression((document.body.clientWidth-this.offsetWidth)/2); top:expression((document.body.clientHeight-this.offsetHeight)/2)">
		<table border="0" cellspacing="10"
			style= "position: relative; z-index:1; left:600; top:200 ">
			<tr>
				<td>登录名：</td>
				<td class="td_right"><input id="txtLoginID" type="text"
					class="txt" value=""></td>
			</tr>
			<tr>
				<td>密 码：</td>
				<td class="td_right"><input id="txtPassWord" type="password"
					class="txt" value=""></td>
			</tr>
			
			<tr>
				<td></td>
				<td class="td_right" valign="top"><input id="button1"
					type="button" class="btnClass_79px_A" value="登录"></td>
			</tr>
		</table>
	</div>
</body>
</html>
