package com.servlet;
/*
 * 基本工具类，包括判断字符串是否为空以及在控制台输出信息两个方法
 */
public class BaseUtil {
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(CharSequence str) {
		if (str == null ||"null".equals(str)|| str.length() == 0) //判断是否已经输入或输入长度是否为空、或输入值为“null”
			return true;
		else
			return false;
	}
	
	public static void LogII(Object iString) {    //在控制台输出信息
		System.out.println("System.out:"+String.valueOf(iString));
		
	}

	
}
