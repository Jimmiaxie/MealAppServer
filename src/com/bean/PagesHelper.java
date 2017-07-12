package com.bean;
/*
 * 页数帮助类,包括表名、主键、列名（字段名）、过滤器、订单、
 * 当前索引号、页数大小
 */
public class PagesHelper {
	private String _tablename = "";   //表名
	private String _primary = "";   //主键
	private String _columnname = "";//列名（字段名）
	private String _filter = "";//过滤器
	private String _order = "";  //顺序
	private int _currentIndex = 0; // 当前索引号为0
	private int _pagesize = 10;//设置每页显示10条记录

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
 * 使用mySQL的limit关键字实现分页，该方法拼接查询语句
 */
	public String ToListString() {
		_order = _order == "" ? _primary : _order;
		
		String SQLPage = "SELECT "+_columnname+" FROM " + _tablename + " WHERE " + _order
				+ " <= ";
		SQLPage += "(SELECT " + _order + " FROM " + _tablename + "  ORDER BY "
				+ _order + " desc LIMIT "       //选择 
				+ (_currentIndex - 1 < 0 ? 0 : (_currentIndex - 1)) + ", 1 )  "  //三目表达式，如果当前索引号为0时取0，否则取当前索引前一位
				+ _filter + " ORDER BY " + _order + " desc LIMIT " + _pagesize;  //取每页记录数大小的数据根据_order排序方式按降序进行排序
		System.out.println(SQLPage);
		return SQLPage;
	}
//根据过滤器和条件获得符合的记录总数
	public String ToCountString() {
		return "select count(1) from " + _tablename + " where 1=1 " + _filter;   // 显示表的记录总数

	}
}
