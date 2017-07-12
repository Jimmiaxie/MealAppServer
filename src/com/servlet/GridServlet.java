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
 * ������ҳ�и��������Ա���ҳ��ʽ��ʾ���ݣ����ݲ�ѯ������ѯ���ݣ����������ڲ�ͬҳ��֮����д���
 */
@SuppressWarnings({ "unchecked", "serial","rawtypes" })
public class GridServlet extends HttpServlet {
	private Session session = null;   //����һ���Ự
	
	/*
	 * ���캯��,��ʼ������
	 */
	public GridServlet() {		
		super();//���ø���Ĺ��췽��
		session = HibernateSessionFactory.getSession(); //����һ���Ự
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
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		session = HibernateSessionFactory.getSession();//�õ�һ���Ự
		request.setCharacterEncoding("UTF-8");// ���öԿͻ���������UTF-8��ʽ�������±���
		response.setCharacterEncoding("UTF-8");// ָ���Է�������Ӧ��UTF-8��ʽ�������±���
		response.setContentType("text/html; charset=UTF-8");//�������ͣ� html���ͺ�UTF-8����
		String action = request.getParameter("Action");//��ô��ݹ�������Ϊ����
		System.out.println("ִ��GridServlet:" + action);
		String sqlString = "";
		ResultSet rs = null;   //����һ������� ����rs
		List list = new ArrayList();//����һ��list�б�
		int pageSize = 10; //����ÿҳ��ʾ10����¼
		int currentpage = 0;   //���õ�ǰҳΪ��0ҳ
		currentpage = Integer.valueOf(request.getParameter("currentpage"));   //��ô��ݹ����ĵ�ǰҳ��
		currentpage = Math.max(currentpage, 1);    //�����ǰҳС��1�����õ�ǰҳΪ1��������Ϊԭֵ

		if (action.equals("getlist")) {   //����б�����
			String msg = "";
			if (request.getParameter("msg") != null) {      //���ݹ�����msg���ݲ�Ϊnull 
				msg = getChinese(request.getParameter("msg"));   //���ݹ���������ת�������ĸ���msg
				System.out.println("msg  " + msg);
			}
			pageSize = 6;   //ÿҳ��ʾ6������
			PagesHelper model = new PagesHelper();   //����һ��PagesHelper��ʵ������
			
			//����model�������Ϣ
			model.setTableName("dishes ");   //����Ϊ�˵���dishes
			model.setColumnName("*");   //����Ϊ������
			model.setOrder("id");     //����id����
			model.setFilter(" and title like '%" + msg + "%'");  //���ù�����Ϊ����msg�Ĳ���
			// �ܹ�������
			int totalCount = Integer.valueOf(String
					.valueOf(HibernateSessionFactory.executeScalar(model
							.ToCountString())));    //���ݹ������������з��ϵ�����������ת����int����
			// ����ҳ
			int pagecount = totalCount % pageSize == 0 ? (totalCount / pageSize)
					: (totalCount / pageSize + 1);//�����������ÿҳ��¼��������������ҳ��Ϊ��������ÿҳ��¼��������Ϊ�ܼ�¼����ÿҳ��¼����һ
			currentpage = Math.min(currentpage, pagecount); //����ǰҳ����ҳ�������Ľ�Сֵ����currentpage
			int start = (currentpage - 1) * pageSize + 1;   //����һ�����Ͷ���
			int limit = pageSize;
			model.setCurrentIndex(start);  //����model����ǰ��������
			model.setPageSize(limit); //����model�����ÿҳ��ʾ��¼������

			rs = HibernateSessionFactory.queryBySql(model.ToListString());   //����model������Խ������ݿ�Ĳ��Ҳ����ؽ����
			System.out.println(model.ToListString());
			
			//request����һϵ�����ԣ�����ҳ��֮�䴫��
			request.setAttribute("datalist",
					HibernateSessionFactory.convertList(rs));  //�����ת���ɵ�list
			request.setAttribute("currentpage", currentpage);  //��ǰҳ��
			request.setAttribute("pagecount", pagecount);   //ҳ��
			request.setAttribute("total", totalCount);    //�ܼ�¼��
			request.getRequestDispatcher("../index.jsp").forward(request,
					response);//��������ת����ʽ��ת��index.jsp������תҳ���ʱ���Ǵ���ԭ��ҳ���request��response��ת�ģ�request����ʼ�մ��ڣ��������´���
		}
		
		if (action.equals("getuserlist")) {  //�õ��û��б�
			String msg = "";
			if (request.getParameter("msg") != null) {    //��ô��ݹ�����msg
				msg = getChinese(request.getParameter("msg"));    //�����ݹ�������Ϣת����������ʽ
				System.out.println("msg  " + msg);
			}
			pageSize = 6;//ÿҳ��¼����Ϊ6��
			PagesHelper model = new PagesHelper();//����һ��ҳ��������ʵ������model
			
			//�������������Ϣ
			model.setTableName("users ");   //�û���Ϣ��users
			model.setColumnName("*");   //������
			model.setOrder("id");   //����id����
			model.setFilter(" and name like '%" + msg + "%'");   //�������ǰ���msg���û���
			// �ܹ�������
			int totalCount = Integer.valueOf(String
					.valueOf(HibernateSessionFactory.executeScalar(model
							.ToCountString())));     //���ݹ������������з��ϵ�����������ת����int����
			// ����ҳ
			int pagecount = totalCount % pageSize == 0 ? (totalCount / pageSize)
					: (totalCount / pageSize + 1);//�����������ÿҳ��¼��������������ҳ��Ϊ��������ÿҳ��¼��������Ϊ�ܼ�¼����ÿҳ��¼����һ
			currentpage = Math.min(currentpage, pagecount);//����ǰҳ����ҳ�������Ľ�Сֵ����currentpage
			int start = (currentpage - 1) * pageSize + 1; //����һ�����Ͷ���
			int limit = pageSize;
			model.setCurrentIndex(start);//����model����ǰ��������
			model.setPageSize(limit);//����model�����ÿҳ��ʾ��¼������
			
			rs = HibernateSessionFactory.queryBySql(model.ToListString()); //����model������Խ������ݿ�Ĳ��Ҳ����ؽ����
			System.out.println(model.ToListString());
			
			//request����һϵ�����ԣ�����ҳ��֮�䴫��
			request.setAttribute("datalist",
					HibernateSessionFactory.convertList(rs));  //�����ת���ɵ�list
			request.setAttribute("currentpage", currentpage);  //��ǰҳ��
			request.setAttribute("pagecount", pagecount);//��ҳ��
			request.setAttribute("total", totalCount);//�ܼ�¼��
			//��������ת����ʽ��ת��userlist.jsp������תҳ���ʱ���Ǵ���ԭ��ҳ���request��response��ת�ģ�request����ʼ�մ��ڣ��������´���
			request.getRequestDispatcher("../userlist.jsp").forward(request,
					response);
		}
		
		if (action.equals("gettypelist")) {   //��ò�Ʒ�����б�
			String msg = "";
			if (request.getParameter("msg") != null) {//��ô��ݹ�����msg��Ϊnull
				msg = getChinese(request.getParameter("msg"));//�����ݹ�������Ϣת����������ʽ
				System.out.println("msg  " + msg);
			}
			pageSize = 6;   //ÿҳ��ʾ6����¼
			PagesHelper model = new PagesHelper();//����һ��ҳ��������ʵ������model
			//����model�������
			model.setTableName("types ");  //��Ʒ���ͱ�types
			model.setColumnName("*");  //������
			model.setOrder("id");    //����id����
			model.setFilter(" and typename like '%" + msg + "%'"); //�������ǰ���msg�Ĳ�Ʒ��������
			// �ܹ�������
			int totalCount = Integer.valueOf(String
					.valueOf(HibernateSessionFactory.executeScalar(model
							.ToCountString()))); //���ݹ������������з��ϵ�����������ת����int����
			// ����ҳ
			int pagecount = totalCount % pageSize == 0 ? (totalCount / pageSize)
					: (totalCount / pageSize + 1);//�����������ÿҳ��¼��������������ҳ��Ϊ��������ÿҳ��¼��������Ϊ�ܼ�¼����ÿҳ��¼����һ
			currentpage = Math.min(currentpage, pagecount);//����ǰҳ����ҳ�������Ľ�Сֵ����currentpage
			int start = (currentpage - 1) * pageSize + 1;//����һ�����Ͷ���
			int limit = pageSize;
			model.setCurrentIndex(start);//����model����ǰ��������
			model.setPageSize(limit);//����model�����ÿҳ��ʾ��¼������

			rs = HibernateSessionFactory.queryBySql(model.ToListString());//����model������Խ������ݿ�Ĳ��Ҳ����ؽ����
			System.out.println(model.ToListString());
			//request����һϵ�����ԣ�����ҳ��֮�䴫��
			request.setAttribute("datalist",
					HibernateSessionFactory.convertList(rs));//�����ת���ɵ�list
			request.setAttribute("currentpage", currentpage); //��ǰҳ��
			request.setAttribute("pagecount", pagecount);   //��ҳ��
			request.setAttribute("total", totalCount);  //�ܼ�¼��
			//��������ת����ʽ��ת��typelist.jsp������תҳ���ʱ���Ǵ���ԭ��ҳ���request��response��ת�ģ�request����ʼ�մ��ڣ��������´���
			request.getRequestDispatcher("../typelist.jsp").forward(request,
					response);
		}
		
		if (action.equals("getorderlist")) {   //��ȡ�����б�
			String msg = "";
			if (request.getParameter("msg") != null) {//��ô��ݹ�����msg��Ϊnull
				msg = getChinese(request.getParameter("msg"));//�����ݹ�������Ϣת����������ʽ
				System.out.println("msg  " + msg);
			}
			pageSize = 6;  //ÿҳ��ʾ6����¼
			PagesHelper model = new PagesHelper();//����һ��ҳ��������ʵ������model
			//����model�������,inner join(��ֵ����) ֻ�����������������ֶ���ȵ�
			//orders��������,ѡ�񶩵���id�к��û���id�С��˵���id���붩����Ķ���id����ͬ���ֶ�
			model.setTableName("orders INNER JOIN users ON orders.userid=users.id INNER JOIN dishes ON dishes.id=orders.dishesid");
			//��������Ϊ�������id���û�������Ϣ�����ж�����ļ۸����������λ�����У�����״̬����1��ʾ���Ѿ���ɡ�״̬��0��ʾ�������С�״̬��������ǡ���ȡ����״̬
			model.setColumnName("orders.id,orders.username,orders.seat,orders.price,orders.amount,users.name,orders.price*orders.amount as total,dishes.title, case status when 1 then '�Ѿ����' WHEN 0 then '������' else '��ȡ��' end status1");
			model.setOrder("orders.id");  //������id������
			model.setFilter(" and users.name like '%" + msg + "%'");//�������ǰ���msg���û���
			// �ܹ�������
			int totalCount = Integer.valueOf(String
					.valueOf(HibernateSessionFactory.executeScalar(model
							.ToCountString())));//���ݹ������������з��ϵ�����������ת����int����
			// ����ҳ
			int pagecount = totalCount % pageSize == 0 ? (totalCount / pageSize)
					: (totalCount / pageSize + 1);//�����������ÿҳ��¼��������������ҳ��Ϊ��������ÿҳ��¼��������Ϊ�ܼ�¼����ÿҳ��¼����һ
			currentpage = Math.min(currentpage, pagecount);//����ǰҳ����ҳ�������Ľ�Сֵ����currentpage
			int start = (currentpage - 1) * pageSize + 1;//����һ�����Ͷ���
			int limit = pageSize;
			model.setCurrentIndex(start);//����model����ĵ�ǰ����
			model.setPageSize(limit);//����model�����ÿҳ��ʾ��¼������
			BaseUtil.LogII(model.ToListString());  //�ڿ���̨��ӡ��model������Ϣ
			
			rs = HibernateSessionFactory.queryBySql(model.ToListString());//����model������Խ������ݿ�Ĳ��Ҳ����ؽ����
			System.out.println(model.ToListString());
			//request����һϵ�����ԣ�����ҳ��֮�䴫��
			request.setAttribute("datalist",
					HibernateSessionFactory.convertList(rs));//�����ת���ɵ�list
			request.setAttribute("currentpage", currentpage);//��ǰҳ��
			request.setAttribute("pagecount", pagecount);//��ҳ��
			request.setAttribute("total", totalCount);//�ܼ�¼��
			//��������ת����ʽ��ת��orderlist.jsp������תҳ���ʱ���Ǵ���ԭ��ҳ���request��response��ת�ģ�request����ʼ�մ��ڣ��������´���
			request.getRequestDispatcher("../orderlist.jsp").forward(request,
					response);
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
			return new String(str.getBytes("ISO8859-1"), "UTF-8");// ����Ϊҳ�汣��һ�µ�UTF-8����ģʽ
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";

		}
	}
/*
 * (non-Javadoc)������Ӧ�ͻ��˵�POST����
 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("ִ��GridServlet");
	}

	@Override
	public void init() throws ServletException {

	}

}
