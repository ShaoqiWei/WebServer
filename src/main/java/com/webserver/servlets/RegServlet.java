package com.webserver.servlets;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * ����reg.html��ע��ҵ��
 * @author ASUS
 *
 */
public class RegServlet extends HttpServlet{
	
	
	
	public void service(HttpRequest request, HttpResponse response){
		
		/*
		 *ע���������
		 *1:��ȡ�û��ύ��ע����Ϣ
		 *2:��ע����Ϣд���ļ�user.det
		 *3:��Ӧ�ͻ���ע��ɹ���ҳ��
		 */
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String nickname = request.getParameter("nickname");
		int age = Integer.parseInt(request.getParameter("age"));
		
		/**
		 * ÿ����¼ռ100�ֽ�,�����û���,����,�ǳ�Ϊ�ַ���,��ռ32�ֽ�,����ռ4�ֽ�
		 * д�뵽user.dat�ļ�
		 */
		try(RandomAccessFile raf = new RandomAccessFile("user.dat","rw");) 
		{
			
			//�ֽ�ָ���ƶ����ļ�ĩβ
			raf.seek(raf.length());
			
			//д�û���
			//1�Ƚ��û���ת�ɶ�Ӧ��һ���ֽ�
			byte[] data = username.getBytes("UTF-8");
			//2������������Ϊ32�ֽ�
			data = Arrays.copyOf(data, 32);
			//3�����ֽ�����һ����д���ļ�
			raf.write(data);
			
			//д����
			data = password.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//д�ǳ�
			data = nickname.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//д����
			raf.writeInt(age);
			
			System.out.println("ע�����!");
			
			forwward("/myweb/reg_success.html", request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
