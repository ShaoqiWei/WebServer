package com.webserver.servlets;

import java.io.File;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * ����servlet�ĳ���,������һЩ���󷽷��͹�ͬ����,�����淶����
 * @author ASUS
 *
 */
public abstract class HttpServlet {
	
	/**
	 * ���๲������
	 */
	
	
	/**
	 * ���󷽷�,�涨�������ʵ�����service(HttpRequest request, HttpResponse response)����
	 * @param request
	 * @param response
	 */
	public abstract void service(HttpRequest request, HttpResponse response);
	
	/**
	 * ��ת��ָ��·��
	 * ע:TomCat��ʵ�ʸ÷�������ת����,����ͨ��request��ȡ.
	 * @param path
	 * @param request
	 * @param response
	 */
	public void forwward(String path, HttpRequest request, HttpResponse response){
		
		response.setEntity(new File("webapps"+path));
	}
}
