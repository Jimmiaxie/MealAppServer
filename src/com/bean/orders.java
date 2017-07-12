package com.bean;
/*
 * 订单信息封装类，包括订单号、用户ID号、菜品ID号、点餐用户名、选择的座位、
 * 价格、数量、订单创建时间以及订单状态
 */
public class orders {
	private int id;
	private int userid;
	private int dishesid;
	private String username;
	private String seat;

	private double price;
	private double amount;
	private String createtime;
private int status;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getDishesid() {
		return dishesid;
	}

	public void setDishesid(int dishesid) {
		this.dishesid = dishesid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = seat;
	}

	
	

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
