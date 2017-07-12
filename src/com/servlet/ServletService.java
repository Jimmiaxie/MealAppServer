package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.bean.dishes;
import com.bean.orders;
import com.bean.seats;
import com.bean.types;
import com.bean.users;
/*
 * 与客户端进行交互类，包括处理用户的登录、生成订单。修改订单等请求，并返回相应的数据给客户端
 */
@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
public class ServletService extends HttpServlet {

	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 时间表示格式为年-月-日   时：分：秒																						
	java.text.SimpleDateFormat formatdate = new java.text.SimpleDateFormat("yyyy-MM-dd");// 时间表示格式为年-月-日
	java.util.Date currentTime = new java.util.Date();// 得到当前系统时间

	private Session session = null;   //声明一个会话
	private HttpServletRequest request; // 客户请求对象
	
/*
 * 构造函数，初始化工作
 */
	public ServletService() {
		super();
		session = HibernateSessionFactory.getSession();//定义一个会话
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
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.request = request;
		session = HibernateSessionFactory.getSession();   //得到一个会话
		session.flush();//强制提交刷新session,清除缓存
		session.clear();//把缓冲区内的全部对象清除，但不包括操作中的对象
		
		request.setCharacterEncoding("UTF-8"); // 设置对客户端请求以UTF-8形式进行重新编码
		response.setCharacterEncoding("UTF-8");// 指定对服务器响应以UTF-8形式进行重新编码
		
		// response.setContentType(MIME)的作用是使客户端浏览器，区分不同种类的数据，并根据不同的MIME调用浏览器内不同的程序嵌入模块来处理相应的数据。
		response.setContentType("text/html; charset=UTF-8");//指定字符编码格式, html类型
		PrintWriter out = response.getWriter();// 当一个Servlet响应的时候将响应信息通过out对象输出到网页上，当响应结束时它自动被关闭。将结果以HTML的形式返回给客户端
		String action = request.getParameter("Action");   //获得客户端传递过来的请求行为
		System.out.println(action);
		String write = "";
		String sqlString = "";
		System.out.print(action);    
		
		if (action.equals("login")) {// 用户登录身份验证请求
			write = login();

		}
		if (action.equals("getOneRow")) {  //获得一行数据请求
			write = getOneRow();
		}
		if (action.equals("Del")) {  //删除信息请求
			write = Del();
		}
		if (action.equals("cancelOrders")) { //取消订单请求
			write = cancelOrders();
		}

		if (action.equals("adminlogin")) {// 管理员登录验证请求
			write = adminlogin();
		}
		if (action.equals("getdisheslist")) {  //获取菜单列表请求
			write = getdisheslist();

		}
		if (action.equals("getmyorderslist")) {  //获取我的订单列表请求
			write = getmyorderslist();
		}
		if (action.equals("ChangeStatus")) {   //改变订单状态请求
			write = changestatus();
		}

		if (action.equals("edit")) {  //编辑菜品信息请求
			write = edit();
		}
		if (action.equals("edituser")) {  //编辑用户信息请求
			write = edituser();
		}
		if (action.equals("edittype")) {  //编辑菜品类型请求
			write = edittype();
		}
		System.out.println(write);  //控制台输出数据
		out.println(write);   //输出数据
		out.flush();	//执行更新
		out.close();   //关闭该流并释放与之关联的所有系统资源

	}
/*
 * (non-Javadoc)用于响应客户端的POST请求
 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		session = HibernateSessionFactory.getSession(); // 得到Session会话对象
		session.flush();// 将session的缓存中的数据与数据库同步
		session.clear();// 清除session中的缓存数据（不管缓存与数据库的同步）
		this.request = request;
		request.setCharacterEncoding("UTF-8");// 设置对客户端请求以UTF-8形式进行重新编码
		response.setCharacterEncoding("UTF-8");// 指定对服务器响应以UTF-8形式进行重新编码
		response.setContentType("text/html; charset=UTF-8");//指定字符编码格式, html类型
		PrintWriter out = response.getWriter();// 输出流，将结果以HTML的形式返回给客户端
		String action = request.getParameter("Action");   //获得客户端传递过来的请求内容
		String write = "";
		
		if (action.equals("register")) {// 注册
			System.out.println(request.getParameter("id"));  //获得传递过来的id
			users model = new users(); // 定义一个用户类实例对象model
			if (request.getParameter("id") == null || request.getParameter("id").equals("0")) { // 判断客户端传递过来的id号是否为空或为0
				model = new users();

			} else {// 客户端传递过来的id号不为空且不为0
				model = (users) (session.createQuery(" from users where id=" + request.getParameter("id")).list()
						.get(0));   //根据传递过来的id进行数据库的查询并将查询结果的第一条数据赋予model
			}
			//将传递过来的登录id、密码、姓名设置为model相关属性
			model.setLoginid(request.getParameter("loginid"));
			model.setPasswords(request.getParameter("password"));
			model.setName(getChinese(request.getParameter("name")));

			Transaction tran = session.beginTransaction();   //开启事务
			session.save(model); // 一个临时对象转变为持久化对象，将model加入缓存
			tran.commit(); // 提交事务
			write = "1";
		}
		if (action.equals("updatePwd")) {// 更改密码请求
			write = updatePwd();
		}

		if (action.equals("createorder")) {// 创建订单请求
			write = createorder();
		}
		out.println(write);     //输出数据
		out.flush();//执行更新
		out.close();//关闭该流并释放与之关联的所有系统资源
	}

	/*
	 * 用户登录处理。根据用户登录id和密码与数据库进行匹配，成功则返回该用户信息
	 */
	private String login() {
		String write = "";
		String loginid = request.getParameter("loginid"); // 获得客户端传递过来的登录ID
		String passwords = request.getParameter("passwords"); // 获得客户端传递过来的密码

		List<users> list = session
				.createQuery(" from users where loginid='" + loginid + "' and passwords='" + passwords + "'").list(); // 根据登录id和密码进行数据库的匹配，并转换且返回一个list列表
		if (list.size() > 0) { // 查询到结果，说明登录信息匹配成功
			write = JSONArray.fromObject(list.get(0)).toString(); // 将List转换为JSONArray数据，再转换成字符串
		}
		return write;
	}

	/*
	 * 管理员登录处理。根据用户登录id和密码与数据库进行匹配，成功则返回字符串“1”，否则返回“0”
	 */
	private String adminlogin() {
		String write = "";
		String loginid = request.getParameter("loginid"); // 获得登录id
		String passwords = request.getParameter("passwords");// 获得登录密码
		List<users> list = session
				.createQuery(" from admins where loginid='" + loginid + "' and passwords='" + passwords + "'").list();// 根据登录id和密码进行数据库的匹配，并转换且返回一个list列表
		if (list.size() > 0) {// 查询到结果，说明登录信息匹配成功
			write = "1";
		} else {   //登录不成功
			write = "0";
		}
		return write;
	}

	/*
	 * 得到我的订单列表，根据用户id查询响应的订单数据并根据订单号以降序方式进行排序，以字符串形式返回
	 */
	private String getmyorderslist() {

		String write = "";
		// inner join(等值连接) 只返回两个表中联结字段相等的行
		String sqlString = "select orders.status, orders.id,orders.userid,orders.username,orders.seat,orders.dishesid,orders.price,orders.amount,orders.createtime,dishes.title,dishes.img_url FROM orders INNER JOIN dishes on orders.dishesid=dishes.id ";
		sqlString += " where  userid =" + request.getParameter("userid"); // 根据用户id查询订单表中菜品id与菜品表中菜品id相同的订单状态、订单id、用户id等相关信息

		sqlString += " order by orders.id desc"; // 根据订单id按降序排序
		ResultSet rs = HibernateSessionFactory.queryBySql(sqlString); // 执行sql语句并返回相应的结果集
		List list = HibernateSessionFactory.convertList(rs);// ResultSet 转list
															// 将结果集转换成列表
		if (list.size() > 0) { // 查询到结果，说明有订单信息
			JSONArray json = JSONArray.fromObject(list);// 将List转换为JSONArray数据
			write = json.toString(); // 将JSon数据转换成字符串
		}
		return write;
	}

	/*
	 * 得到菜单列表，根据传递过来的数据信息获得相对应的菜单列表信息
	 */
	private String getdisheslist() {
		String write = "";
		String sqlString = "from dishes where 1=1 "; // 从菜单列表中获取信息
		if (request.getParameter("msg") != null) { // 传递过来的msg信息不为null
			sqlString += " and title like '%" + getChinese(request.getParameter("msg")) + "%'"; // 查询包含传递过来的数据的信息
		}
		sqlString += " order by id desc"; // 降序排序
		List list = session.createQuery(sqlString).list(); // 根据sql语句得到查询结果并转换成list形式
		if (list.size() > 0) { // 查询到结果，说明有菜单信息
			JSONArray json = JSONArray.fromObject(list);// 将List转换为JSONArray数据
			write = json.toString(); // 将JSon数据转换成字符串
		}
		return write;
	}

	/*
	 * 编辑菜单信息，如果id不为0，说明该信息已经存在就进行更新，否则在数据库保存该信息。
	 */
	private String edit() throws UnsupportedEncodingException {
		int id = Integer.valueOf((request.getParameter("ID"))); // 得到传递过来的id号
		dishes model; // 声明菜品信息类对象实例model
		if (id == 0) { // 说明该菜品还没有存在
			model = new dishes(); // 定义

		} else {
			model = (dishes) (session.createQuery(" from dishes where id=" + id).list().get(0)); // 根据id号进行数据库的信息匹配并将查询结果的第一条数据赋予model
		}

		if (request.getParameter("img_url") != null && request.getParameter("img_url").length() > 0) { // 请求图片地址不为空或长度不为0
			model.setImg_url(request.getParameter("img_url")); // 设置model的图片地址属性
		}
		// 设置model的相关属性
		model.setIntro(getChinese(request.getParameter("intro")));
		model.setTitle(getChinese(request.getParameter("title")));
		model.setPrice(Float.valueOf(request.getParameter("price")));
		model.setAmount(Float.valueOf(request.getParameter("amount")));
		model.setTypeid(Integer.valueOf(request.getParameter("typeid")));
		model.setTypename(getChinese(request.getParameter("typename")));
		Transaction tran = session.beginTransaction(); // 开启事务

		if (id != 0) { // 说明数据库已经存在该菜品信息
			session.update(model); // 更新数据
		} else {// 数据库没有该菜品信息的相关信息
			session.save(model);// 保存信息
		}
		tran.commit(); // 提交事务
		return "1";
	}

	/*
	 * 取消订单操作，根据id查找相关信息，将该订单中的座位属性改为未被选择，在从数据库中删除该订单的相关信息
	 */
	private String cancelOrders() {
		int id = Integer.valueOf((request.getParameter("ID"))); // 获取id
		orders model; // 声明订单信息类的实例对象
		model = (orders) (session.createQuery(" from orders where id=" + id).list().get(0)); // 根据id号进行数据库的信息匹配并将查询结果的第一条数据赋予model
		String[] ss = model.getSeat().split(","); // 以“，”为分隔符，将座位信息分成多个子字符串并放入数组中
		for (int i = 0; i < ss.length; i++) { // 遍历数组
			seats s = (seats) session.createQuery(" from seats where id=" + ss[i]).list().get(0); // 根据数组中的数据查找数据库中匹配的数据并去第一条数据赋予座位信息类实例对象s
			s.setState(0); // 设置s的座位属性为0，变为未被选择状态
			session.save(s); // 保存信息
		}
		Transaction tran = session.beginTransaction(); // 开启事务
		session.delete(model); // 从数据库删除该对象相关数据
		tran.commit(); // 提交事务
		return "1";
	}

	/*
	 * 修改订单状态，根据id查找相关的订单信息，并将该订单的状态进行修改，提交保存到数据库中
	 */
	private String changestatus() {
		int id = Integer.valueOf((request.getParameter("ID"))); // 获取id
		int status = Integer.valueOf((request.getParameter("status"))); // 获取订单状态信息
		orders model; // 声明一个订单信息类的实例对象model
		model = (orders) (session.createQuery(" from orders where id=" + id).list().get(0)); // 根据id号进行数据库的信息匹配并将查询结果的第一条数据赋予model
		model.setStatus(status); // 设置model的状态属性为status
		Transaction tran = session.beginTransaction();// 开启事务
		session.save(model);// 保存model数据信息
		tran.commit();// 提交事务
		return "1";
	}

	/*
	 * 编辑用户信息。根据id判断是否为新用户，给用户类实例对象设置相关属性，并与数据库进行交互
	 */
	private String edituser() throws UnsupportedEncodingException {
		int id = Integer.valueOf((request.getParameter("ID"))); // 获取id号
		users model; // 声明一个用户信息类的实例对象model
		if (id == 0) { // id为0，说明是新用户
			model = new users(); // 定义一个对象

		} else { // 已经存在的用户
			model = (users) (session.createQuery(" from users where id=" + id).list().get(0)); // 根据id查询相对应的用户信息赋予model对象
		}
		// 将传递过来的数据设置成model的相关信息
		model.setLoginid(getChinese(request.getParameter("loginid")));// 设置登录id
		model.setName(getChinese(request.getParameter("name"))); // 设置用户名
		model.setPasswords(request.getParameter("passwords")); // 设置密码
		Transaction tran = session.beginTransaction(); // 开启事务
		if (id != 0) { // 不是新用户
			session.update(model); // 更新用户信息
		} else {// 新用户
			session.save(model); // 保存用户信息
		}
		tran.commit();// 提交事务
		return "1";
	}

	/**
	 * 编辑菜品类型，根据id判断是否为新菜品，给菜品类型类对象实例设置菜品类型信息，并提交数据库
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String edittype() throws UnsupportedEncodingException {
		int id = Integer.valueOf((request.getParameter("ID"))); // 获取id
		types model; // 声明一个菜品信息类实例对象
		if (id == 0) { // 新菜品
			model = new types();
			// 已经存在的菜品
		} else {
			model = (types) (session.createQuery(" from types where id=" + id).list().get(0)); // 根据id查询相对应的菜品类型信息赋予model对象
		}

		model.setTypename(getChinese(request.getParameter("typename"))); // 将传递过来的类型名称设置为model的菜品类型

		Transaction tran = session.beginTransaction(); // 开启事务
		if (id != 0) { // 已经存在的菜品
			session.update(model);// 更新信息
		} else { // 新菜品
			session.save(model);// 保存信息
		}
		tran.commit();// 提交事务
		return "1";
	}

	/*
	 * 创建订单。根据id判断是否为新订单，给订单类对象实例设置订单相关信息，并提交数据库
	 */
	private String createorder() throws UnsupportedEncodingException {
		orders model = null;// 声明一个订单信息类的实例对象
		List list = session.createQuery(" from orders where id=" + request.getParameter("id")).list(); // 根据传递过来的id得到查询结果并转换成list形式
		if (list.size() == 0) { // 未查询到结果，即没有生成过该订单
			model = new orders(); // 定义
			model.setCreatetime(formatdate.format(currentTime)); // 设置model的创建时间属性为当前时间（以年-月-日的形式）
		} else { // 订单已经存在
			model = (orders) list.get(0); // 获取list的第一条数据赋予model
		}
		dishes dishesModel = (dishes) session.createQuery(" from dishes where id=" + request.getParameter("dishesid"))
				.list().get(0); // 根据传递过来的菜品id查询该菜品的相关信息并赋予菜品信息类的实例对象
		// 设置订单类实例对象model的相关属性，包括用户id、用户名等信息
		model.setUserid(Integer.valueOf(request.getParameter("userid")));
		model.setUsername(getChinese(request.getParameter("username")));
		model.setAmount(Double.valueOf(request.getParameter("amount")));
		model.setPrice(dishesModel.getPrice());
		model.setSeat(request.getParameter("seat"));
		model.setDishesid(dishesModel.getId());

		String[] ss = model.getSeat().split(","); // 以“，”为分隔符将model的座位信息分成多个子字符串并放入数组中
		for (int i = 0; i < ss.length; i++) { // 遍历数组
			seats s = (seats) session.createQuery(" from seats where id=" + ss[i]).list().get(0); // 根据数组中的数据查询数据库座位表中的数据并将结果赋予座位类实例对象s
			s.setState(1); // 设置s的座位属性为被选中状态
			session.save(s); // 保存该座位信息
		}

		Transaction tran = session.beginTransaction();// 开启事务
		session.save(model); // 保存订单信息
		tran.commit();// 提交事务
		return "1";
	}

	/**
	 * 修改密码，根据用户登录id和密码先进行判断是否信息正确，正确则再将新密码提交数据库完成修改密码的操作
	 * 
	 * @return
	 */
	public String updatePwd() throws UnsupportedEncodingException {
		List list = session.createQuery(" from users where loginid='" + request.getParameter("loginid")
				+ "' and passwords='" + request.getParameter("passwords") + "'").list(); // 根据传递过来的登录id和密码得到查询结果并转换成list形式
		if (list.size() == 0) {
			return "-1";// 账号或密码错误
		} else { // 信息匹配成功
			users model = (users) list.get(0); // 将list的第一条数据信息赋予用户类实例对象model
			model.setPasswords(request.getParameter("passwords_new")); // 将model的密码属性设置为传递过来的新密码
			Transaction tran = session.beginTransaction();// 开启事务
			session.save(model); // 保存信息
			tran.commit();// 提交事务
			return "1";// 修改成功
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
			return URLDecoder.decode(str, "UTF-8"); // 设置为页面保持一致的UTF-8编码模式
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";

		}
	}

	/**
	 * 公用的获取一行数据方法，根据传递过来的id和表名得到相对应数据信息
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getOneRow() throws UnsupportedEncodingException {
		List list = null;
		if (request.getParameter("ID") == null) { // 传递过来的id为0
			list = session.createQuery(" from " + request.getParameter("Table")).list();
		} else { // id不为0
			list = session
					.createQuery(" from " + request.getParameter("Table") + " where id=" + request.getParameter("ID"))
					.list(); // 根据传递过来的id和表名得到查询结果并转换成list形式
		}

		JSONArray json = JSONArray.fromObject(list); // 将list转换成JSonArray
		return json.toString(); // JSonArray转换成字符串类型
	}

	/*
	 * 删除信息，根据传递过来的id和表名删除表中相对应的信息
	 */
	public String Del() {
		int ID = Integer.valueOf(request.getParameter("ID")); // 获取id
		String Table = request.getParameter("Table"); // 获取表名
		String PK_Name = "id";
		String sql = "delete from " + Table + " where " + PK_Name + "=" + ID; // SQL语句，根据id删除该条信息
		HibernateSessionFactory.updateExecute(sql); // 执行操作
		return "1";

	}

	/*
	 * (non-Javadoc)初始化
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {

	}

}
