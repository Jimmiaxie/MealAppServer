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
 * ��ͻ��˽��н����࣬���������û��ĵ�¼�����ɶ������޸Ķ��������󣬲�������Ӧ�����ݸ��ͻ���
 */
@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
public class ServletService extends HttpServlet {

	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // ʱ���ʾ��ʽΪ��-��-��   ʱ���֣���																						
	java.text.SimpleDateFormat formatdate = new java.text.SimpleDateFormat("yyyy-MM-dd");// ʱ���ʾ��ʽΪ��-��-��
	java.util.Date currentTime = new java.util.Date();// �õ���ǰϵͳʱ��

	private Session session = null;   //����һ���Ự
	private HttpServletRequest request; // �ͻ��������
	
/*
 * ���캯������ʼ������
 */
	public ServletService() {
		super();
		session = HibernateSessionFactory.getSession();//����һ���Ự
	}
/*
 * (non-Javadoc)Servlet�����˳���������ʱ�������ͷ�ռ�õ���Դ
 * @see javax.servlet.GenericServlet#destroy()
 */
	@Override
	public void destroy() {
		super.destroy();
	}
/*
 * (non-Javadoc)������Ӧ�ͻ��˵�GET����
 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.request = request;
		session = HibernateSessionFactory.getSession();   //�õ�һ���Ự
		session.flush();//ǿ���ύˢ��session,�������
		session.clear();//�ѻ������ڵ�ȫ������������������������еĶ���
		
		request.setCharacterEncoding("UTF-8"); // ���öԿͻ���������UTF-8��ʽ�������±���
		response.setCharacterEncoding("UTF-8");// ָ���Է�������Ӧ��UTF-8��ʽ�������±���
		
		// response.setContentType(MIME)��������ʹ�ͻ�������������ֲ�ͬ��������ݣ������ݲ�ͬ��MIME����������ڲ�ͬ�ĳ���Ƕ��ģ����������Ӧ�����ݡ�
		response.setContentType("text/html; charset=UTF-8");//ָ���ַ������ʽ, html����
		PrintWriter out = response.getWriter();// ��һ��Servlet��Ӧ��ʱ����Ӧ��Ϣͨ��out�����������ҳ�ϣ�����Ӧ����ʱ���Զ����رա��������HTML����ʽ���ظ��ͻ���
		String action = request.getParameter("Action");   //��ÿͻ��˴��ݹ�����������Ϊ
		System.out.println(action);
		String write = "";
		String sqlString = "";
		System.out.print(action);    
		
		if (action.equals("login")) {// �û���¼�����֤����
			write = login();

		}
		if (action.equals("getOneRow")) {  //���һ����������
			write = getOneRow();
		}
		if (action.equals("Del")) {  //ɾ����Ϣ����
			write = Del();
		}
		if (action.equals("cancelOrders")) { //ȡ����������
			write = cancelOrders();
		}

		if (action.equals("adminlogin")) {// ����Ա��¼��֤����
			write = adminlogin();
		}
		if (action.equals("getdisheslist")) {  //��ȡ�˵��б�����
			write = getdisheslist();

		}
		if (action.equals("getmyorderslist")) {  //��ȡ�ҵĶ����б�����
			write = getmyorderslist();
		}
		if (action.equals("ChangeStatus")) {   //�ı䶩��״̬����
			write = changestatus();
		}

		if (action.equals("edit")) {  //�༭��Ʒ��Ϣ����
			write = edit();
		}
		if (action.equals("edituser")) {  //�༭�û���Ϣ����
			write = edituser();
		}
		if (action.equals("edittype")) {  //�༭��Ʒ��������
			write = edittype();
		}
		System.out.println(write);  //����̨�������
		out.println(write);   //�������
		out.flush();	//ִ�и���
		out.close();   //�رո������ͷ���֮����������ϵͳ��Դ

	}
/*
 * (non-Javadoc)������Ӧ�ͻ��˵�POST����
 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		session = HibernateSessionFactory.getSession(); // �õ�Session�Ự����
		session.flush();// ��session�Ļ����е����������ݿ�ͬ��
		session.clear();// ���session�еĻ������ݣ����ܻ��������ݿ��ͬ����
		this.request = request;
		request.setCharacterEncoding("UTF-8");// ���öԿͻ���������UTF-8��ʽ�������±���
		response.setCharacterEncoding("UTF-8");// ָ���Է�������Ӧ��UTF-8��ʽ�������±���
		response.setContentType("text/html; charset=UTF-8");//ָ���ַ������ʽ, html����
		PrintWriter out = response.getWriter();// ��������������HTML����ʽ���ظ��ͻ���
		String action = request.getParameter("Action");   //��ÿͻ��˴��ݹ�������������
		String write = "";
		
		if (action.equals("register")) {// ע��
			System.out.println(request.getParameter("id"));  //��ô��ݹ�����id
			users model = new users(); // ����һ���û���ʵ������model
			if (request.getParameter("id") == null || request.getParameter("id").equals("0")) { // �жϿͻ��˴��ݹ�����id���Ƿ�Ϊ�ջ�Ϊ0
				model = new users();

			} else {// �ͻ��˴��ݹ�����id�Ų�Ϊ���Ҳ�Ϊ0
				model = (users) (session.createQuery(" from users where id=" + request.getParameter("id")).list()
						.get(0));   //���ݴ��ݹ�����id�������ݿ�Ĳ�ѯ������ѯ����ĵ�һ�����ݸ���model
			}
			//�����ݹ����ĵ�¼id�����롢��������Ϊmodel�������
			model.setLoginid(request.getParameter("loginid"));
			model.setPasswords(request.getParameter("password"));
			model.setName(getChinese(request.getParameter("name")));

			Transaction tran = session.beginTransaction();   //��������
			session.save(model); // һ����ʱ����ת��Ϊ�־û����󣬽�model���뻺��
			tran.commit(); // �ύ����
			write = "1";
		}
		if (action.equals("updatePwd")) {// ������������
			write = updatePwd();
		}

		if (action.equals("createorder")) {// ������������
			write = createorder();
		}
		out.println(write);     //�������
		out.flush();//ִ�и���
		out.close();//�رո������ͷ���֮����������ϵͳ��Դ
	}

	/*
	 * �û���¼���������û���¼id�����������ݿ����ƥ�䣬�ɹ��򷵻ظ��û���Ϣ
	 */
	private String login() {
		String write = "";
		String loginid = request.getParameter("loginid"); // ��ÿͻ��˴��ݹ����ĵ�¼ID
		String passwords = request.getParameter("passwords"); // ��ÿͻ��˴��ݹ���������

		List<users> list = session
				.createQuery(" from users where loginid='" + loginid + "' and passwords='" + passwords + "'").list(); // ���ݵ�¼id������������ݿ��ƥ�䣬��ת���ҷ���һ��list�б�
		if (list.size() > 0) { // ��ѯ�������˵����¼��Ϣƥ��ɹ�
			write = JSONArray.fromObject(list.get(0)).toString(); // ��Listת��ΪJSONArray���ݣ���ת�����ַ���
		}
		return write;
	}

	/*
	 * ����Ա��¼���������û���¼id�����������ݿ����ƥ�䣬�ɹ��򷵻��ַ�����1�������򷵻ء�0��
	 */
	private String adminlogin() {
		String write = "";
		String loginid = request.getParameter("loginid"); // ��õ�¼id
		String passwords = request.getParameter("passwords");// ��õ�¼����
		List<users> list = session
				.createQuery(" from admins where loginid='" + loginid + "' and passwords='" + passwords + "'").list();// ���ݵ�¼id������������ݿ��ƥ�䣬��ת���ҷ���һ��list�б�
		if (list.size() > 0) {// ��ѯ�������˵����¼��Ϣƥ��ɹ�
			write = "1";
		} else {   //��¼���ɹ�
			write = "0";
		}
		return write;
	}

	/*
	 * �õ��ҵĶ����б������û�id��ѯ��Ӧ�Ķ������ݲ����ݶ������Խ���ʽ�����������ַ�����ʽ����
	 */
	private String getmyorderslist() {

		String write = "";
		// inner join(��ֵ����) ֻ�����������������ֶ���ȵ���
		String sqlString = "select orders.status, orders.id,orders.userid,orders.username,orders.seat,orders.dishesid,orders.price,orders.amount,orders.createtime,dishes.title,dishes.img_url FROM orders INNER JOIN dishes on orders.dishesid=dishes.id ";
		sqlString += " where  userid =" + request.getParameter("userid"); // �����û�id��ѯ�������в�Ʒid���Ʒ���в�Ʒid��ͬ�Ķ���״̬������id���û�id�������Ϣ

		sqlString += " order by orders.id desc"; // ���ݶ���id����������
		ResultSet rs = HibernateSessionFactory.queryBySql(sqlString); // ִ��sql��䲢������Ӧ�Ľ����
		List list = HibernateSessionFactory.convertList(rs);// ResultSet תlist
															// �������ת�����б�
		if (list.size() > 0) { // ��ѯ�������˵���ж�����Ϣ
			JSONArray json = JSONArray.fromObject(list);// ��Listת��ΪJSONArray����
			write = json.toString(); // ��JSon����ת�����ַ���
		}
		return write;
	}

	/*
	 * �õ��˵��б����ݴ��ݹ�����������Ϣ������Ӧ�Ĳ˵��б���Ϣ
	 */
	private String getdisheslist() {
		String write = "";
		String sqlString = "from dishes where 1=1 "; // �Ӳ˵��б��л�ȡ��Ϣ
		if (request.getParameter("msg") != null) { // ���ݹ�����msg��Ϣ��Ϊnull
			sqlString += " and title like '%" + getChinese(request.getParameter("msg")) + "%'"; // ��ѯ�������ݹ��������ݵ���Ϣ
		}
		sqlString += " order by id desc"; // ��������
		List list = session.createQuery(sqlString).list(); // ����sql���õ���ѯ�����ת����list��ʽ
		if (list.size() > 0) { // ��ѯ�������˵���в˵���Ϣ
			JSONArray json = JSONArray.fromObject(list);// ��Listת��ΪJSONArray����
			write = json.toString(); // ��JSon����ת�����ַ���
		}
		return write;
	}

	/*
	 * �༭�˵���Ϣ�����id��Ϊ0��˵������Ϣ�Ѿ����ھͽ��и��£����������ݿⱣ�����Ϣ��
	 */
	private String edit() throws UnsupportedEncodingException {
		int id = Integer.valueOf((request.getParameter("ID"))); // �õ����ݹ�����id��
		dishes model; // ������Ʒ��Ϣ�����ʵ��model
		if (id == 0) { // ˵���ò�Ʒ��û�д���
			model = new dishes(); // ����

		} else {
			model = (dishes) (session.createQuery(" from dishes where id=" + id).list().get(0)); // ����id�Ž������ݿ����Ϣƥ�䲢����ѯ����ĵ�һ�����ݸ���model
		}

		if (request.getParameter("img_url") != null && request.getParameter("img_url").length() > 0) { // ����ͼƬ��ַ��Ϊ�ջ򳤶Ȳ�Ϊ0
			model.setImg_url(request.getParameter("img_url")); // ����model��ͼƬ��ַ����
		}
		// ����model���������
		model.setIntro(getChinese(request.getParameter("intro")));
		model.setTitle(getChinese(request.getParameter("title")));
		model.setPrice(Float.valueOf(request.getParameter("price")));
		model.setAmount(Float.valueOf(request.getParameter("amount")));
		model.setTypeid(Integer.valueOf(request.getParameter("typeid")));
		model.setTypename(getChinese(request.getParameter("typename")));
		Transaction tran = session.beginTransaction(); // ��������

		if (id != 0) { // ˵�����ݿ��Ѿ����ڸò�Ʒ��Ϣ
			session.update(model); // ��������
		} else {// ���ݿ�û�иò�Ʒ��Ϣ�������Ϣ
			session.save(model);// ������Ϣ
		}
		tran.commit(); // �ύ����
		return "1";
	}

	/*
	 * ȡ����������������id���������Ϣ�����ö����е���λ���Ը�Ϊδ��ѡ���ڴ����ݿ���ɾ���ö����������Ϣ
	 */
	private String cancelOrders() {
		int id = Integer.valueOf((request.getParameter("ID"))); // ��ȡid
		orders model; // ����������Ϣ���ʵ������
		model = (orders) (session.createQuery(" from orders where id=" + id).list().get(0)); // ����id�Ž������ݿ����Ϣƥ�䲢����ѯ����ĵ�һ�����ݸ���model
		String[] ss = model.getSeat().split(","); // �ԡ�����Ϊ�ָ���������λ��Ϣ�ֳɶ�����ַ���������������
		for (int i = 0; i < ss.length; i++) { // ��������
			seats s = (seats) session.createQuery(" from seats where id=" + ss[i]).list().get(0); // ���������е����ݲ������ݿ���ƥ������ݲ�ȥ��һ�����ݸ�����λ��Ϣ��ʵ������s
			s.setState(0); // ����s����λ����Ϊ0����Ϊδ��ѡ��״̬
			session.save(s); // ������Ϣ
		}
		Transaction tran = session.beginTransaction(); // ��������
		session.delete(model); // �����ݿ�ɾ���ö����������
		tran.commit(); // �ύ����
		return "1";
	}

	/*
	 * �޸Ķ���״̬������id������صĶ�����Ϣ�������ö�����״̬�����޸ģ��ύ���浽���ݿ���
	 */
	private String changestatus() {
		int id = Integer.valueOf((request.getParameter("ID"))); // ��ȡid
		int status = Integer.valueOf((request.getParameter("status"))); // ��ȡ����״̬��Ϣ
		orders model; // ����һ��������Ϣ���ʵ������model
		model = (orders) (session.createQuery(" from orders where id=" + id).list().get(0)); // ����id�Ž������ݿ����Ϣƥ�䲢����ѯ����ĵ�һ�����ݸ���model
		model.setStatus(status); // ����model��״̬����Ϊstatus
		Transaction tran = session.beginTransaction();// ��������
		session.save(model);// ����model������Ϣ
		tran.commit();// �ύ����
		return "1";
	}

	/*
	 * �༭�û���Ϣ������id�ж��Ƿ�Ϊ���û������û���ʵ����������������ԣ��������ݿ���н���
	 */
	private String edituser() throws UnsupportedEncodingException {
		int id = Integer.valueOf((request.getParameter("ID"))); // ��ȡid��
		users model; // ����һ���û���Ϣ���ʵ������model
		if (id == 0) { // idΪ0��˵�������û�
			model = new users(); // ����һ������

		} else { // �Ѿ����ڵ��û�
			model = (users) (session.createQuery(" from users where id=" + id).list().get(0)); // ����id��ѯ���Ӧ���û���Ϣ����model����
		}
		// �����ݹ������������ó�model�������Ϣ
		model.setLoginid(getChinese(request.getParameter("loginid")));// ���õ�¼id
		model.setName(getChinese(request.getParameter("name"))); // �����û���
		model.setPasswords(request.getParameter("passwords")); // ��������
		Transaction tran = session.beginTransaction(); // ��������
		if (id != 0) { // �������û�
			session.update(model); // �����û���Ϣ
		} else {// ���û�
			session.save(model); // �����û���Ϣ
		}
		tran.commit();// �ύ����
		return "1";
	}

	/**
	 * �༭��Ʒ���ͣ�����id�ж��Ƿ�Ϊ�²�Ʒ������Ʒ���������ʵ�����ò�Ʒ������Ϣ�����ύ���ݿ�
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String edittype() throws UnsupportedEncodingException {
		int id = Integer.valueOf((request.getParameter("ID"))); // ��ȡid
		types model; // ����һ����Ʒ��Ϣ��ʵ������
		if (id == 0) { // �²�Ʒ
			model = new types();
			// �Ѿ����ڵĲ�Ʒ
		} else {
			model = (types) (session.createQuery(" from types where id=" + id).list().get(0)); // ����id��ѯ���Ӧ�Ĳ�Ʒ������Ϣ����model����
		}

		model.setTypename(getChinese(request.getParameter("typename"))); // �����ݹ�����������������Ϊmodel�Ĳ�Ʒ����

		Transaction tran = session.beginTransaction(); // ��������
		if (id != 0) { // �Ѿ����ڵĲ�Ʒ
			session.update(model);// ������Ϣ
		} else { // �²�Ʒ
			session.save(model);// ������Ϣ
		}
		tran.commit();// �ύ����
		return "1";
	}

	/*
	 * ��������������id�ж��Ƿ�Ϊ�¶����������������ʵ�����ö��������Ϣ�����ύ���ݿ�
	 */
	private String createorder() throws UnsupportedEncodingException {
		orders model = null;// ����һ��������Ϣ���ʵ������
		List list = session.createQuery(" from orders where id=" + request.getParameter("id")).list(); // ���ݴ��ݹ�����id�õ���ѯ�����ת����list��ʽ
		if (list.size() == 0) { // δ��ѯ���������û�����ɹ��ö���
			model = new orders(); // ����
			model.setCreatetime(formatdate.format(currentTime)); // ����model�Ĵ���ʱ������Ϊ��ǰʱ�䣨����-��-�յ���ʽ��
		} else { // �����Ѿ�����
			model = (orders) list.get(0); // ��ȡlist�ĵ�һ�����ݸ���model
		}
		dishes dishesModel = (dishes) session.createQuery(" from dishes where id=" + request.getParameter("dishesid"))
				.list().get(0); // ���ݴ��ݹ����Ĳ�Ʒid��ѯ�ò�Ʒ�������Ϣ�������Ʒ��Ϣ���ʵ������
		// ���ö�����ʵ������model��������ԣ������û�id���û�������Ϣ
		model.setUserid(Integer.valueOf(request.getParameter("userid")));
		model.setUsername(getChinese(request.getParameter("username")));
		model.setAmount(Double.valueOf(request.getParameter("amount")));
		model.setPrice(dishesModel.getPrice());
		model.setSeat(request.getParameter("seat"));
		model.setDishesid(dishesModel.getId());

		String[] ss = model.getSeat().split(","); // �ԡ�����Ϊ�ָ�����model����λ��Ϣ�ֳɶ�����ַ���������������
		for (int i = 0; i < ss.length; i++) { // ��������
			seats s = (seats) session.createQuery(" from seats where id=" + ss[i]).list().get(0); // ���������е����ݲ�ѯ���ݿ���λ���е����ݲ������������λ��ʵ������s
			s.setState(1); // ����s����λ����Ϊ��ѡ��״̬
			session.save(s); // �������λ��Ϣ
		}

		Transaction tran = session.beginTransaction();// ��������
		session.save(model); // ���涩����Ϣ
		tran.commit();// �ύ����
		return "1";
	}

	/**
	 * �޸����룬�����û���¼id�������Ƚ����ж��Ƿ���Ϣ��ȷ����ȷ���ٽ��������ύ���ݿ�����޸�����Ĳ���
	 * 
	 * @return
	 */
	public String updatePwd() throws UnsupportedEncodingException {
		List list = session.createQuery(" from users where loginid='" + request.getParameter("loginid")
				+ "' and passwords='" + request.getParameter("passwords") + "'").list(); // ���ݴ��ݹ����ĵ�¼id������õ���ѯ�����ת����list��ʽ
		if (list.size() == 0) {
			return "-1";// �˺Ż��������
		} else { // ��Ϣƥ��ɹ�
			users model = (users) list.get(0); // ��list�ĵ�һ��������Ϣ�����û���ʵ������model
			model.setPasswords(request.getParameter("passwords_new")); // ��model��������������Ϊ���ݹ�����������
			Transaction tran = session.beginTransaction();// ��������
			session.save(model); // ������Ϣ
			tran.commit();// �ύ����
			return "1";// �޸ĳɹ�
		}

	}

	/**
	 * ȡ������
	 * 
	 * @param ԭ�ַ�
	 * @return
	 */
	private String getChinese(String str) {
		if (str == null) {
			return "";
		}
		try {
			return URLDecoder.decode(str, "UTF-8"); // ����Ϊҳ�汣��һ�µ�UTF-8����ģʽ
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";

		}
	}

	/**
	 * ���õĻ�ȡһ�����ݷ��������ݴ��ݹ�����id�ͱ����õ����Ӧ������Ϣ
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getOneRow() throws UnsupportedEncodingException {
		List list = null;
		if (request.getParameter("ID") == null) { // ���ݹ�����idΪ0
			list = session.createQuery(" from " + request.getParameter("Table")).list();
		} else { // id��Ϊ0
			list = session
					.createQuery(" from " + request.getParameter("Table") + " where id=" + request.getParameter("ID"))
					.list(); // ���ݴ��ݹ�����id�ͱ����õ���ѯ�����ת����list��ʽ
		}

		JSONArray json = JSONArray.fromObject(list); // ��listת����JSonArray
		return json.toString(); // JSonArrayת�����ַ�������
	}

	/*
	 * ɾ����Ϣ�����ݴ��ݹ�����id�ͱ���ɾ���������Ӧ����Ϣ
	 */
	public String Del() {
		int ID = Integer.valueOf(request.getParameter("ID")); // ��ȡid
		String Table = request.getParameter("Table"); // ��ȡ����
		String PK_Name = "id";
		String sql = "delete from " + Table + " where " + PK_Name + "=" + ID; // SQL��䣬����idɾ��������Ϣ
		HibernateSessionFactory.updateExecute(sql); // ִ�в���
		return "1";

	}

	/*
	 * (non-Javadoc)��ʼ��
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {

	}

}
