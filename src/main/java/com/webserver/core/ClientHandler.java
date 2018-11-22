package com.webserver.core;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlets.HttpServlet;

/**
 * 处理客户端请求
 */
public class ClientHandler implements Runnable {
	private Socket socket;
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	public void run() {
		try {
			/*
			 *主流程:	//http://localhost:8088/myweb/index.html
			 *1:解析请求
			 *2:处理请求
			 *3:发送响应 
			 */
			
			//1准备工作
			//1.1解析请求,创建请求对象
			HttpRequest request = new HttpRequest(socket);
			//1.2创建响应对象
			HttpResponse response = new HttpResponse(socket);
			
			//2处理请求
			//2.1:获取请求的资源路径
			String url = request.getRequsetURI();
			System.out.println("url===>"+url);
			
			String servletName = ServerContext.getServletName(url);
			
			//判断url请求类型(是资源还是逻辑),如果存在就是资源,不存在则是路径
			if (servletName != null) {
				
				//1.加载类
				Class<?> cls = Class.forName(servletName);
				
				//2.实例化类(向上造型)
				HttpServlet servlet = (HttpServlet)cls.newInstance();
				
				//3.调用方法
				servlet.service(request, response);
			
			}else{
				//2.2:根据资源路径去webapps目录中寻找该资源
				File file = new File("webapps"+url);
				
				//2.3:如果路径不存在
				if(!file.exists()){
					response.setStatusCode(404);
					System.out.println("资源不存在");
					file = new File("webapps"+"/root/404.html");
				}
				//2.3加载传输文件
				response.setEntity(file);
			}
			
			//3响应客户端
			response.flush();
		
		} catch (EmptyRequestException e) {
			/*
			 * 主要解决客户端发送空请求时,服务器拆分字符串会发生数组越界的异常
			 */
			System.err.println("空请求!");
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			//与客户端断开连接
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
