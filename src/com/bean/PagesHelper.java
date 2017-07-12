package com.bean;
/*
 * ҳ��������,�����������������������ֶ���������������������
 * ��ǰ�����š�ҳ����С
 */
public class PagesHelper {
	private String _tablename = "";   //����
	private String _primary = "";   //����
	private String _columnname = "";//�������ֶ�����
	private String _filter = "";//������
	private String _order = "";  //˳��
	private int _currentIndex = 0; // ��ǰ������Ϊ0
	private int _pagesize = 10;//����ÿҳ��ʾ10����¼

	public void setTableName(String _tablename) {
		this._tablename = _tablename;
	}

	public void setPrimary(String _primary) {
		this._primary = _primary;
	}

	public void setCurrentIndex(int _currentIndex) {
		this._currentIndex = _currentIndex;
	}

	public void setPageSize(int _pagesize) {
		this._pagesize = _pagesize;
	}

	public void setColumnName(String _columnname) {
		this._columnname = _columnname;
	}

	public void setFilter(String _filter) {
		this._filter = _filter;
	}

	public void setOrder(String _order) {
		this._order = _order;
	}
/*
 * ʹ��mySQL��limit�ؼ���ʵ�ַ�ҳ���÷���ƴ�Ӳ�ѯ���
 */
	public String ToListString() {
		_order = _order == "" ? _primary : _order;
		
		String SQLPage = "SELECT "+_columnname+" FROM " + _tablename + " WHERE " + _order
				+ " <= ";
		SQLPage += "(SELECT " + _order + " FROM " + _tablename + "  ORDER BY "
				+ _order + " desc LIMIT "       //ѡ�� 
				+ (_currentIndex - 1 < 0 ? 0 : (_currentIndex - 1)) + ", 1 )  "  //��Ŀ���ʽ�������ǰ������Ϊ0ʱȡ0������ȡ��ǰ����ǰһλ
				+ _filter + " ORDER BY " + _order + " desc LIMIT " + _pagesize;  //ȡÿҳ��¼����С�����ݸ���_order����ʽ�������������
		System.out.println(SQLPage);
		return SQLPage;
	}
//���ݹ�������������÷��ϵļ�¼����
	public String ToCountString() {
		return "select count(1) from " + _tablename + " where 1=1 " + _filter;   // ��ʾ��ļ�¼����

	}
}
