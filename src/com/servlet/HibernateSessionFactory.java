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
 * Hibernate�Ự�����࣬���ڻỰSession�¶����������õ��Ự���رջỰ���ؽ��Ự�����ȣ���Ҫ��Hibernate��ܵ�ʹ��
 */
public class HibernateSessionFactory {

	private static String CONFIG_FILE_LOCATION = "/hibernate.cfg.xml"; // ָ������Ϊhibernate�������ļ�
	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();// �߳��ڹ���Session��ThreadLocalͨ����ȫ�ֵģ�֧�ַ���
	private static Configuration configuration = new Configuration();// ����Configuration����
	private static org.hibernate.SessionFactory sessionFactory; // ����һ���Ự��������
	private static String configFile = CONFIG_FILE_LOCATION;// �����ļ�
	// ��̬�������г�ʼ��
	static {
		try {
			configuration.configure(configFile); // ��ȡhibernate.cfg.xml�������ļ�����ɳ�ʼ��
			sessionFactory = configuration.buildSessionFactory(); // ����Ự��������
		} catch (Exception e) {
			System.err.println("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}

	/*
	 * ���캯��
	 */
	private HibernateSessionFactory() {
	}

	/*
	 * �õ�Session�Ự�ķ���
	 */
	public static Session getSession() throws HibernateException {
		Session session = threadLocal.get(); // ��ȡ��ǰ�߳��ڹ����Session

		if (session == null || !session.isOpen()) {// ����Ự����Ϊ�ջ��߻Ựû�д�
			if (sessionFactory == null) { // �Ự����Ϊ��
				rebuildSessionFactory(); // �ؽ��Ự����
			}
			session = (sessionFactory != null) ? sessionFactory.openSession() : null; // ����Ự��������Ϊ�գ���򿪻Ự��������Ϊ��
			threadLocal.set(session); // �ܴ�Session
		}

		return session;
	}

	/*
	 * �ؽ��Ự����������������ȡhibernate.cfg.xml�������ļ�����ɳ�ʼ���Լ������Ự����
	 */
	public static void rebuildSessionFactory() {
		try {
			configuration.configure(configFile);// ��ȡhibernate.cfg.xml�������ļ�
			sessionFactory = configuration.buildSessionFactory();// �����Ự����
		} catch (Exception e) {
			System.err.println("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}

	/*
	 * �رջỰ����
	 */
	public static void closeSession() throws HibernateException {
		Session session = threadLocal.get();
		threadLocal.set(null);

		if (session != null) {
			session.close(); // �رջỰ
		}
	}

	// �õ��Ự��������
	public static org.hibernate.SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	// ���������ļ�
	public static void setConfigFile(String configFile) {
		HibernateSessionFactory.configFile = configFile;
		sessionFactory = null;
	}

	/*
	 * �õ����ö���
	 */
	public static Configuration getConfiguration() {
		return configuration;
	}

	/*
	 * ����SQL�������ݿ���в�������������Ӧ�Ľ����
	 */
	@SuppressWarnings("deprecation")
	public static ResultSet queryBySql(String sql) {
		ResultSet rs = null;
		try {
			Connection con = getSession().connection(); // �������ݿ�
			Statement sta = con.createStatement();// �������ݿ����Ӷ���
			rs = sta.executeQuery(sql);// ִ��SQL����ѯ�����ؽ����

		} catch (SQLException e) {
			e.printStackTrace();

		}
		return rs;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	/**
	 * ResultSet תlist �������ת�����б�
	 * 
	 * @param rs
	 * @return
	 */
	public static List convertList(ResultSet rs) {
		List listOfRows = new ArrayList(); // ����һ��list
		try {
			ResultSetMetaData md = rs.getMetaData(); // �õ������rs���е����ƺ�������Ϣ
			int num = md.getColumnCount(); // �õ�������
			while (rs.next()) {
				Map mapOfColValues = new HashMap(num); // ����һ��Hashmap
				for (int i = 1; i <= num; i++) {
					BaseUtil.LogII(md.getColumnName(i)); // ��ӡ��ǰ�е�����
					mapOfColValues.put(md.getColumnName(i), rs.getObject(i));// �����ݿ���Ϣ��ŵ�map��
				}
				listOfRows.add(mapOfColValues);// list���������

			}
		} catch (Exception e) {
			System.out.println(e.getMessage()); // �ڿ���̨��������쳣�������Ϣ
		}
		return listOfRows;
	}

	/*
	 * ����ִ�в����������ظ��µļ�¼����
	 */
	public static int updateExecute(String sql) {
		int result = 0;
		try {
			Session session = getSession(); // �õ��Ự����
			Connection con = session.connection();// ����mysql���ݿ�
			Transaction tran = session.beginTransaction();// ��������
			tran.begin(); // ��ʼ����
			Statement sta = con.createStatement(); // �������ݿ����Ӷ���
			result = sta.executeUpdate(sql);// ִ��SQL��䲢��ֵ�Ǹ��µ�����
			tran.commit(); // �ύ����
		} catch (SQLException e) {

			e.printStackTrace();

		}
		return result;
	}

	/*
	 * ����SQL����ѯ���������в�ѯ������򷵻ص�һ����¼�ĵ�һ�е�ֵ
	 */
	public static String executeScalar(String sql) {
		ResultSet rs = queryBySql(sql);// ����SQL����ѯ�����ؽ����
		String s = "";
		try {
			while (rs.next()) { // ��ѯ�����
				s = rs.getString(1); // ������е�һ�е�ֵ
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return s;
	}

}