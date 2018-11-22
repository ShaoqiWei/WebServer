package com.webserver.servlets;

import java.io.RandomAccessFile;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

public class LoginServlet extends HttpServlet{
	public void service(HttpRequest request, HttpResponse response){
		System.out.println("µÇÂ½Ò³Ãæ");
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		try (
			RandomAccessFile fis = new RandomAccessFile("user.dat","r");
			){
			
			String name = null;
			String pass = null;
			byte[] data = new byte[100];
			while((fis.read(data)) != -1){
				name = new String(data,0,32,"UTF-8").trim();
				if(name.equals(username)){
					pass = new String(data,32,32,"UTF-8").trim();
					if(pass.equals(password)){
						
						forwward("/myweb/login_success.html", request, response);
						return;
					}
				}
			}
			forwward("/myweb/login_fail.html", request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
