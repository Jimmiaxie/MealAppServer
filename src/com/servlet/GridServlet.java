package com.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.bean.PagesHelper;
/*
 * 用于网页中各种数据以表格分页形式显示数据，根据查询条件查询数据，并将数据在不同页面之间进行传递
 */
@SuppressWarnings({ "unchecked", "serial","rawtypes" })
public class GridServlet extends HttpServlet {
	private Session session = null;   //声明一个会话
	
	/*
	 * 构造函数,初始化工作
	 */
	public GridServlet() {		
		super();//调用父类的构造方法
		session = HibernateSessionFactory.getSession(); //定义一个会话
	}
/*
 * (non-Javadoc)Servlet对象退出生命周期时，负责释放占用的资源
 * @see javax.servlet.GenericServlet#destroy()
 */
	@Override
	public void destroy() {
		super.destroy();
	}
/*
 * (non-Javadoc)用于响应客户端的GET请求
 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		session = HibernateSessionFactory.getSession();//得到一个会话
		request.setCharacterEncoding("UTF-8");// 设置对客户端请求以UTF-8形式进行重新编码
		response.setCharacterEncoding("UTF-8");// 指定对服务器响应以UTF-8形式进行重新编码
		response.setContentType("text/html; charset=UTF-8");//设置类型， html类型和UTF-8编码
		String action = request.getParameter("Action");//获得传递过来的行为请求
		System.out.println("执行GridServlet:" + action);
		String sqlString = "";
		ResultSet rs = null;   //声明一个结果集 对象rs
		List list = new ArrayList();//定义一个list列表
		int pageSize = 10; //设置每页显示10条记录
		int currentpage = 0;   //设置当前页为第0页
		currentpage = Integer.valueOf(request.getParameter("currentpage"));   //获得传递过来的当前页数
		currentpage = Math.max(currentpage, 1);    //如果当前页小于1则设置当前页为1，否则设为原值

		if (action.equals("getlist")) {   //获得列表请求
			String msg = "";
			if (request.getParameter("msg") != null) {      //传递过来的msg数据不为null 
				msg = getChinese(request.getParameter("msg"));   //传递过来的数据转换成中文赋予msg
				System.out.println("msg  " + msg);
			}
			pageSize = 6;   //每页显示6条数据
			PagesHelper model = new PagesHelper();   //定义一个PagesHelper的实例对象
			
			//设置model的相关信息
			model.setTableName("dishes ");   //表名为菜单表dishes
			model.setColumnName("*");   //设置为所有列
			model.setOrder("id");     //根据id排序
			model.setFilter(" and title like '%" + msg + "%'");  //设置过滤器为包含msg的菜名
			// 总共多少条
			int totalCount = Integer.valueOf(String
					.valueOf(HibernateSessionFactory.executeScalar(model
							.ToCountString())));    //根据过滤器查找所有符合的数据总数并转换成int类型
			// 多少页
			int pagecount = totalCount % pageSize == 0 ? (totalCount / pageSize)
					: (totalCount / pageSize + 1);//如果总条数是每页记录数的整数倍，则页数为总条数除每页记录数，否则为总记录数除每页记录数加一
			currentpage = Math.min(currentpage, pagecount); //将当前页数和页数总数的较小值赋予currentpage
			int start = (currentpage - 1) * pageSize + 1;   //定义一个整型对象
			int limit = pageSize;
			model.setCurrentIndex(start);  //设置model对象当前的索引号
			model.setPageSize(limit); //设置model对象的每页显示记录的条数

			rs = HibernateSessionFactory.queryBySql(model.ToListString());   //根据model相关属性进行数据库的查找并返回结果集
			System.out.println(model.ToListString());
			
			//request设置一系列属性，用于页面之间传递
			request.setAttribute("datalist",
					HibernateSessionFactory.convertList(rs));  //结果集转换成的list
			request.setAttribute("currentpage", currentpage);  //当前页数
			request.setAttribute("pagecount", pagecount);   //页数
			request.setAttribute("total", totalCount);    //总记录数
			request.getRequestDispatcher("../index.jsp").forward(request,
					response);//采用请求转发方式跳转到index.jsp，在跳转页面的时候是带着原来页面的request和response跳转的，request对象始终存在，不会重新创建
		}
		
		if (action.equals("getuserlist")) {  //得到用户列表
			String msg = "";
			if (request.getParameter("msg") != null) {    //获得传递过来的msg
				msg = getChinese(request.getParameter("msg"));    //将传递过来的信息转换成中文形式
				System.out.println("msg  " + msg);
			}
			pageSize = 6;//每页记录数据为6条
			PagesHelper model = new PagesHelper();//定义一个页数帮助类实例对象model
			
			//设置相关属性信息
			model.setTableName("users ");   //用户信息表users
			model.setColumnName("*");   //所有列
			model.setOrder("id");   //根据id排序
			model.setFilter(" and name like '%" + msg + "%'");   //过滤器是包含msg的用户名
			// 总共多少条
			int totalCount = Integer.valueOf(String
					.valueOf(HibernateSessionFactory.executeScalar(model
							.ToCountString())));     //根据过滤器查找所有符合的数据总数并转换成int类型
			// 多少页
			int pagecount = totalCount % pageSize == 0 ? (totalCount / pageSize)
					: (totalCount / pageSize + 1);//如果总条数是每页记录数的整数倍，则页数为总条数除每页记录数，否则为总记录数除每页记录数加一
			currentpage = Math.min(currentpage, pagecount);//将当前页数和页数总数的较小值赋予currentpage
			int start = (currentpage - 1) * pageSize + 1; //定义一个整型对象
			int limit = pageSize;
			model.setCurrentIndex(start);//设置model对象当前的索引号
			model.setPageSize(limit);//设置model对象的每页显示记录的条数
			
			rs = HibernateSessionFactory.queryBySql(model.ToListString()); //根据model相关属性进行数据库的查找并返回结果集
			System.out.println(model.ToListString());
			
			//request设置一系列属性，用于页面之间传递
			request.setAttribute("datalist",
					HibernateSessionFactory.convertList(rs));  //结果集转换成的list
			request.setAttribute("currentpage", currentpage);  //当前页数
			request.setAttribute("pagecount", pagecount);//总页数
			request.setAttribute("total", totalCount);//总记录数
			//采用请求转发方式跳转到userlist.jsp，在跳转页面的时候是带着原来页面的request和response跳转的，request对象始终存在，不会重新创建
			request.getRequestDispatcher("../userlist.jsp").forward(request,
					response);
		}
		
		if (action.equals("gettypelist")) {   //获得菜品类型列表
			String msg = "";
			if (request.getParameter("msg") != null) {//获得传递过来的msg不为null
				msg = getChinese(request.getParameter("msg"));//将传递过来的信息转换成中文形式
				System.out.println("msg  " + msg);
			}
			pageSize = 6;   //每页显示6条记录
			PagesHelper model = new PagesHelper();//定义一个页数帮助类实例对象model
			//设置model相关属性
			model.setTableName("types ");  //菜品类型表types
			model.setColumnName("*");  //所有列
			model.setOrder("id");    //根据id排序
			model.setFilter(" and typename like '%" + msg + "%'"); //过滤器是包含msg的菜品类型名称
			// 总共多少条
			int totalCount = Integer.valueOf(String
					.valueOf(HibernateSessionFactory.executeScalar(model
							.ToCountString()))); //根据过滤器查找所有符合的数据总数并转换成int类型
			// 多少页
			int pagecount = totalCount % pageSize == 0 ? (totalCount / pageSize)
					: (totalCount / pageSize + 1);//如果总条数是每页记录数的整数倍，则页数为总条数除每页记录数，否则为总记录数除每页记录数加一
			currentpage = Math.min(currentpage, pagecount);//将当前页数和页数总数的较小值赋予currentpage
			int start = (currentpage - 1) * pageSize + 1;//定义一个整型对象
			int limit = pageSize;
			model.setCurrentIndex(start);//设置model对象当前的索引号
			model.setPageSize(limit);//设置model对象的每页显示记录的条数

			rs = HibernateSessionFactory.queryBySql(model.ToListString());//根据model相关属性进行数据库的查找并返回结果集
			System.out.println(model.ToListString());
			//request设置一系列属性，用于页面之间传递
			request.setAttribute("datalist",
					HibernateSessionFactory.convertList(rs));//结果集转换成的list
			request.setAttribute("currentpage", currentpage); //当前页数
			request.setAttribute("pagecount", pagecount);   //总页数
			request.setAttribute("total", totalCount);  //总记录数
			//采用请求转发方式跳转到typelist.jsp，在跳转页面的时候是带着原来页面的request和response跳转的，request对象始终存在，不会重新创建
			request.getRequestDispatcher("../typelist.jsp").forward(request,
					response);
		}
		
		if (action.equals("getorderlist")) {   //获取订单列表
			String msg = "";
			if (request.getParameter("msg") != null) {//获得传递过来的msg不为null
				msg = getChinese(request.getParameter("msg"));//将传递过来的信息转换成中文形式
				System.out.println("msg  " + msg);
			}
			pageSize = 6;  //每页显示6条记录
			PagesHelper model = new PagesHelper();//定义一个页数帮助类实例对象model
			//设置model相关属性,inner join(等值连接) 只返回两个表中联结字段相等的
			//orders订单表中,选择订单表id列和用户表id列、菜单表id列与订单表的订单id列相同的字段
			model.setTableName("orders INNER JOIN users ON orders.userid=users.id INNER JOIN dishes ON dishes.id=orders.dishesid");
			//设置列名为订单表的id、用户名等信息，其中订单表的价格乘上数量座位总数列，订单状态列中1表示“已经完成”状态，0表示“进行中”状态，否则就是“已取消”状态
			model.setColumnName("orders.id,orders.username,orders.seat,orders.price,orders.amount,users.name,orders.price*orders.amount as total,dishes.title, case status when 1 then '已经完成' WHEN 0 then '进行中' else '已取消' end status1");
			model.setOrder("orders.id");  //按订单id号排序
			model.setFilter(" and users.name like '%" + msg + "%'");//过滤器是包含msg的用户名
			// 总共多少条
			int totalCount = Integer.valueOf(String
					.valueOf(HibernateSessionFactory.executeScalar(model
							.ToCountString())));//根据过滤器查找所有符合的数据总数并转换成int类型
			// 多少页
			int pagecount = totalCount % pageSize == 0 ? (totalCount / pageSize)
					: (totalCount / pageSize + 1);//如果总条数是每页记录数的整数倍，则页数为总条数除每页记录数，否则为总记录数除每页记录数加一
			currentpage = Math.min(currentpage, pagecount);//将当前页数和页数总数的较小值赋予currentpage
			int start = (currentpage - 1) * pageSize + 1;//定义一个整型对象
			int limit = pageSize;
			model.setCurrentIndex(start);//设置model对象的当前索引
			model.setPageSize(limit);//设置model对象的每页显示记录的条数
			BaseUtil.LogII(model.ToListString());  //在控制台打印出model对象信息
			
			rs = HibernateSessionFactory.queryBySql(model.ToListString());//根据model相关属性进行数据库的查找并返回结果集
			System.out.println(model.ToListString());
			//request设置一系列属性，用于页面之间传递
			request.setAttribute("datalist",
					HibernateSessionFactory.convertList(rs));//结果集转换成的list
			request.setAttribute("currentpage", currentpage);//当前页数
			request.setAttribute("pagecount", pagecount);//总页数
			request.setAttribute("total", totalCount);//总记录数
			//采用请求转发方式跳转到orderlist.jsp，在跳转页面的时候是带着原来页面的request和response跳转的，request对象始终存在，不会重新创建
			request.getRequestDispatcher("../orderlist.jsp").forward(request,
					response);
		}

	}

	/**
	 * 取得中文
	 * 
	 * @param 原字符
	 * @return
	 */
	private String getChinese(String str) {
		if (str == null) {
			return "";
		}
		try {
			return new String(str.getBytes("ISO8859-1"), "UTF-8");// 设置为页面保持一致的UTF-8编码模式
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";

		}
	}
/*
 * (non-Javadoc)用于响应客户端的POST请求
 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("执行GridServlet");
	}

	@Override
	public void init() throws ServletException {

	}

}
