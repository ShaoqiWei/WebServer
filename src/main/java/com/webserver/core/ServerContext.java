package com.webserver.core;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * 加载保存请求路径与反射类信息
 * key: /myweb/login
 * value: com.webserver.servlets.LoginServlet
 */
public class ServerContext {
	
	private static Map<String,String> servletMapping = new HashMap<String, String>();
	
	static{
		initServletMapping();
	}
	
	/**
	 * 加载保存请求路径与反射类信息
	 * key: /myweb/login
	 * value: com.webserver.servlets.LoginServlet
	 */
	private static void initServletMapping(){

		try {
			
			//1.创建读取方法
			SAXReader reader = new SAXReader();
			
			//2.读取xml文件
			Document doc = reader.read(new FileInputStream("conf/Servlets.xml"));
			
			//3.读取根节点
			Element root = doc.getRootElement();
			
			//4获取根标签下的所有员工标签<mime-mapping>的实例
			@SuppressWarnings("unchecked")
			List<Element> mime = root.elements("mime-mapping");

			/*
			 * 取出每个<mime-mapping>的信息
			 */
			for(Element emp:mime){
				//获取子标签的内容
				String extension = emp.elementText("extension");
				String mime_type = emp.elementText("mime-type");
				servletMapping.put(extension, mime_type);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 根据请求路径获取对应Servlet的类名字
	 * @param url
	 * @return Servlet Class Name
	 */
	public static String getServletName(String url){
		return servletMapping.get(url);
	}
}
