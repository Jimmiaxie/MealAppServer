package com.servlet;
/*
 * ���������࣬�����ж��ַ����Ƿ�Ϊ���Լ��ڿ���̨�����Ϣ��������
 */
public class BaseUtil {
	/**
	 * �ж��ַ����Ƿ�Ϊ��
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(CharSequence str) {
		if (str == null ||"null".equals(str)|| str.length() == 0) //�ж��Ƿ��Ѿ���������볤���Ƿ�Ϊ�ա�������ֵΪ��null��
			return true;
		else
			return false;
	}
	
	public static void LogII(Object iString) {    //�ڿ���̨�����Ϣ
		System.out.println("System.out:"+String.valueOf(iString));
		
	}

	
}
