package com.webserver.core;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlets.HttpServlet;

/**
 * ����ͻ�������
 */
public class ClientHandler implements Runnable {
	private Socket socket;
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	public void run() {
		try {
			/*
			 *������:	//http://localhost:8088/myweb/index.html
			 *1:��������
			 *2:��������
			 *3:������Ӧ 
			 */
			
			//1׼������
			//1.1��������,�����������
			HttpRequest request = new HttpRequest(socket);
			//1.2������Ӧ����
			HttpResponse response = new HttpResponse(socket);
			
			//2��������
			//2.1:��ȡ�������Դ·��
			String url = request.getRequsetURI();
			System.out.println("url===>"+url);
			
			String servletName = ServerContext.getServletName(url);
			
			//�ж�url��������(����Դ�����߼�),������ھ�����Դ,����������·��
			if (servletName != null) {
				
				//1.������
				Class<?> cls = Class.forName(servletName);
				
				//2.ʵ������(��������)
				HttpServlet servlet = (HttpServlet)cls.newInstance();
				
				//3.���÷���
				servlet.service(request, response);
			
			}else{
				//2.2:������Դ·��ȥwebappsĿ¼��Ѱ�Ҹ���Դ
				File file = new File("webapps"+url);
				
				//2.3:���·��������
				if(!file.exists()){
					response.setStatusCode(404);
					System.out.println("��Դ������");
					file = new File("webapps"+"/root/404.html");
				}
				//2.3���ش����ļ�
				response.setEntity(file);
			}
			
			//3��Ӧ�ͻ���
			response.flush();
		
		} catch (EmptyRequestException e) {
			/*
			 * ��Ҫ����ͻ��˷��Ϳ�����ʱ,����������ַ����ᷢ������Խ����쳣
			 */
			System.err.println("������!");
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			//��ͻ��˶Ͽ�����
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
