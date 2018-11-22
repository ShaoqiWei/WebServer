package com.webserver.servlets;

import java.io.File;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 所有servlet的超类,定义了一些抽象方法和共同属性,用来规范子类
 * @author ASUS
 *
 */
public abstract class HttpServlet {
	
	/**
	 * 子类共有属性
	 */
	
	
	/**
	 * 抽象方法,规定子类必须实现这个service(HttpRequest request, HttpResponse response)方法
	 * @param request
	 * @param response
	 */
	public abstract void service(HttpRequest request, HttpResponse response);
	
	/**
	 * 跳转到指定路径
	 * 注:TomCat中实际该方法属于转发器,可以通过request获取.
	 * @param path
	 * @param request
	 * @param response
	 */
	public void forwward(String path, HttpRequest request, HttpResponse response){
		
		response.setEntity(new File("webapps"+path));
	}
}
