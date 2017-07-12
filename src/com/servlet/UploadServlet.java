package com.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/*
 * 图片上传处理类。
 */
public class UploadServlet extends HttpServlet {
	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 时间表示格式为年-月-日
																									// 时：分：秒
	java.text.SimpleDateFormat formatdate = new java.text.SimpleDateFormat("yyyy-MM-dd");// 时间表示格式为年-月-日
	java.util.Date currentTime = new java.util.Date();// 得到当前系统时间
	/*
	 * (non-Javadoc)响应客户端所有请求
	 * 
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {

			request.setCharacterEncoding("UTF-8"); // 设置处理请求参数的编码格式
			response.setContentType("text/html;charset=UTF-8"); // 指定字符编码格式,设置Content-Type字段值
			PrintWriter out = response.getWriter();// 将结果以HTML的形式返回给客户端

			// 下面的代码开始使用Commons-UploadFile组件处理上传的文件数据
			FileItemFactory factory = new DiskFileItemFactory(); // 建立FileItemFactory对象
			ServletFileUpload upload = new ServletFileUpload(factory);// 创建处理工具
			// 分析请求，并得到上传文件的FileItem对象
			List<FileItem> items = upload.parseRequest(request);
			String uploadPath = getServletContext().getRealPath("/UploadFile");// 得到该应用下的upload目录在服务器上的绝对路径
			uploadPath += "\\";
			File file = new File(uploadPath);// 定义文件
			if (!file.exists()) { // 文件不存在
				file.mkdir(); // 创建目录
			}
			System.out.println(uploadPath);
			String filename = ""; // 上传文件保存到服务器的文件名
			InputStream is = null; // 当前上传文件的InputStream对象
			// 循环处理上传文件
			for (FileItem item : items) {
				// 处理普通的表单域
				if (item.isFormField()) {
					if (item.getFieldName().equals("filename")) {
						// 如果新文件不为空，将其保存在filename中
						if (!item.getString().equals(""))
							filename = item.getString("UTF-8");
					}
				}
				// 处理上传文件
				else if (item.getName() != null && !item.getName().equals("")) {
					// 从客户端发送过来的上传文件路径中截取文件名
					filename = item.getName().substring(item.getName().lastIndexOf("\\") + 1);
					is = item.getInputStream(); // 得到上传文件的InputStream对象
				}
			}
			System.out.println(filename);
			UUID uuid = UUID.randomUUID();//随机生成全局唯一标识符
			String uuidFileName = uuid + filename.substring(filename.lastIndexOf("."));//截取上传文件名的文件类型和uuid组成唯一文件名
			// 将路径和上传文件名组合成完整的服务端路径
			filename = uploadPath + uuidFileName;
			System.out.println(filename);
			// 如果服务器已经存在和上传文件同名的文件，则删除该文件
			if (new File(filename).exists()) {
				new File(filename).delete();   
			}
			if (!filename.equals("")) {
				// 用FileOutputStream打开服务端的上传文件
				FileOutputStream fos = new FileOutputStream(filename);
				byte[] buffer = new byte[8192]; // 每次读8K字节
				int count = 0;
				// 开始读取上传文件的字节，并将其输出到服务端的上传文件输出流中
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);

				}
				fos.close();//关闭文件输出流
				is.close();//关闭输入流
				out.println(uuidFileName);  //输出唯一文件名
			}
		} catch (Exception e) {

		}
	}
}
