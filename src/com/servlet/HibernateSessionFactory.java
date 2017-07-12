package com.servlet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
/*
 * Hibernate会话工厂类，用于会话Session德尔管理，包括得到会话、关闭会话、重建会话工厂等，主要是Hibernate框架的使用
 */
public class HibernateSessionFactory {

	private static String CONFIG_FILE_LOCATION = "/hibernate.cfg.xml"; // 指定常量为hibernate的配置文件
	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();// 线程内共享Session，ThreadLocal通常是全局的，支持泛型
	private static Configuration configuration = new Configuration();// 创建Configuration对象
	private static org.hibernate.SessionFactory sessionFactory; // 声明一个会话工厂对象
	private static String configFile = CONFIG_FILE_LOCATION;// 配置文件
	// 静态代码块进行初始化
	static {
		try {
			configuration.configure(configFile); // 读取hibernate.cfg.xml主配置文件，完成初始化
			sessionFactory = configuration.buildSessionFactory(); // 定义会话工厂对象
		} catch (Exception e) {
			System.err.println("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}

	/*
	 * 构造函数
	 */
	private HibernateSessionFactory() {
	}

	/*
	 * 得到Session会话的方法
	 */
	public static Session getSession() throws HibernateException {
		Session session = threadLocal.get(); // 获取当前线程内共享的Session

		if (session == null || !session.isOpen()) {// 如果会话对象为空或者会话没有打开
			if (sessionFactory == null) { // 会话工厂为空
				rebuildSessionFactory(); // 重建会话工厂
			}
			session = (sessionFactory != null) ? sessionFactory.openSession() : null; // 如果会话工厂对象不为空，则打开会话，否则置为空
			threadLocal.set(session); // 曹村Session
		}

		return session;
	}

	/*
	 * 重建会话工厂方法，包括读取hibernate.cfg.xml主配置文件，完成初始化以及建立会话工厂
	 */
	public static void rebuildSessionFactory() {
		try {
			configuration.configure(configFile);// 读取hibernate.cfg.xml主配置文件
			sessionFactory = configuration.buildSessionFactory();// 建立会话工厂
		} catch (Exception e) {
			System.err.println("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}

	/*
	 * 关闭会话方法
	 */
	public static void closeSession() throws HibernateException {
		Session session = threadLocal.get();
		threadLocal.set(null);

		if (session != null) {
			session.close(); // 关闭会话
		}
	}

	// 得到会话工厂对象
	public static org.hibernate.SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	// 设置配置文件
	public static void setConfigFile(String configFile) {
		HibernateSessionFactory.configFile = configFile;
		sessionFactory = null;
	}

	/*
	 * 得到配置对象
	 */
	public static Configuration getConfiguration() {
		return configuration;
	}

	/*
	 * 根据SQL语句对数据库进行操作，并返回相应的结果集
	 */
	@SuppressWarnings("deprecation")
	public static ResultSet queryBySql(String sql) {
		ResultSet rs = null;
		try {
			Connection con = getSession().connection(); // 连接数据库
			Statement sta = con.createStatement();// 创建数据库链接对象
			rs = sta.executeQuery(sql);// 执行SQL语句查询并返回结果集

		} catch (SQLException e) {
			e.printStackTrace();

		}
		return rs;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	/**
	 * ResultSet 转list 将结果集转换成列表
	 * 
	 * @param rs
	 * @return
	 */
	public static List convertList(ResultSet rs) {
		List listOfRows = new ArrayList(); // 定义一个list
		try {
			ResultSetMetaData md = rs.getMetaData(); // 得到结果集rs中列的名称和类型信息
			int num = md.getColumnCount(); // 得到总列数
			while (rs.next()) {
				Map mapOfColValues = new HashMap(num); // 定义一个Hashmap
				for (int i = 1; i <= num; i++) {
					BaseUtil.LogII(md.getColumnName(i)); // 打印当前列的列名
					mapOfColValues.put(md.getColumnName(i), rs.getObject(i));// 将数据库信息存放到map中
				}
				listOfRows.add(mapOfColValues);// list中添加数据

			}
		} catch (Exception e) {
			System.out.println(e.getMessage()); // 在控制台输出产生异常的相关信息
		}
		return listOfRows;
	}

	/*
	 * 更新执行操作，并返回更新的记录条数
	 */
	public static int updateExecute(String sql) {
		int result = 0;
		try {
			Session session = getSession(); // 得到会话对象
			Connection con = session.connection();// 连接mysql数据库
			Transaction tran = session.beginTransaction();// 开启事务
			tran.begin(); // 开始事务
			Statement sta = con.createStatement(); // 创建数据库链接对象
			result = sta.executeUpdate(sql);// 执行SQL语句并回值是更新的条数
			tran.commit(); // 提交事务
		} catch (SQLException e) {

			e.printStackTrace();

		}
		return result;
	}

	/*
	 * 根据SQL语句查询结果，如果有查询到结果则返回第一条记录的第一列的值
	 */
	public static String executeScalar(String sql) {
		ResultSet rs = queryBySql(sql);// 根据SQL语句查询并返回结果集
		String s = "";
		try {
			while (rs.next()) { // 查询到结果
				s = rs.getString(1); // 结果集中第一列的值
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return s;
	}

}