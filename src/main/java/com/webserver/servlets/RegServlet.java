package com.webserver.servlets;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 处理reg.html的注册业务
 * @author ASUS
 *
 */
public class RegServlet extends HttpServlet{
	
	
	
	public void service(HttpRequest request, HttpResponse response){
		
		/*
		 *注册大致流程
		 *1:获取用户提交的注册信息
		 *2:将注册信息写入文件user.det
		 *3:响应客户端注册成功的页面
		 */
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String nickname = request.getParameter("nickname");
		int age = Integer.parseInt(request.getParameter("age"));
		
		/**
		 * 每条记录占100字节,其中用户名,密码,昵称为字符串,各占32字节,年龄占4字节
		 * 写入到user.dat文件
		 */
		try(RandomAccessFile raf = new RandomAccessFile("user.dat","rw");) 
		{
			
			//现将指针移动到文件末尾
			raf.seek(raf.length());
			
			//写用户名
			//1先将用户名转成对应的一组字节
			byte[] data = username.getBytes("UTF-8");
			//2将该数组扩容为32字节
			data = Arrays.copyOf(data, 32);
			//3将该字节数组一次性写入文件
			raf.write(data);
			
			//写密码
			data = password.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//写昵称
			data = nickname.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//写年龄
			raf.writeInt(age);
			
			System.out.println("注册完毕!");
			
			forwward("/myweb/reg_success.html", request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
