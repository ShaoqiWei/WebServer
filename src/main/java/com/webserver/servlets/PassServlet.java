package com.webserver.servlets;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;


/**
 * –ﬁ∏ƒ√‹¬Î
 * @author ASUS
 *
 */

public class PassServlet extends HttpServlet{
	public void service(HttpRequest request, HttpResponse response){
		System.out.println("–ﬁ∏ƒ√‹¬Î“≥√Ê");
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String newpass1 = request.getParameter("newpass1");;
		String newpass2 = request.getParameter("newpass2");;
		
		try (
			RandomAccessFile fis = new RandomAccessFile("user.dat","rw");
			){
			
			String name = null;
			String pass = null;
			
			byte[] data = new byte[100];
			int leng = 0;
			
			while((fis.read(data)) != -1){
				leng += data.length;
				name = new String(data,0,32,"UTF-8").trim();
				
				switch (0) {
				case 0:
					if(username.equals(name))
						pass = new String(data,32,32,"UTF-8").trim();
					else
						break;

				case 1:
					if(!password.equals(pass))
						break;
				
				case 2:
					if(newpass1.equals(newpass2)){
						fis.seek(leng - 100 + 32);
						byte[] base = newpass1.getBytes("UTF-8");
						base = Arrays.copyOf(base, 32);
						fis.write(base);
						forwward("/myweb/pass_success.html", request, response);
						return;
						
					}else{
						
						forwward("/myweb/pass_fail.html", request, response);
						return;
					}
				}
			}
			
			forwward("/myweb/pass_fail2.html", request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
