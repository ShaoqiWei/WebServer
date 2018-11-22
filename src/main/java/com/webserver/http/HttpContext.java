package com.webserver.http;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Http协议相关内容定义
 * 
 * @author ASUS
 *
 */
public class HttpContext {
	
	/**
	 * 回车符CR
	 */
	public static final int CR = 13;
	/**
	 * 换行符LF
	 */
	public static final int LF = 10;
	
	
	/**
	 * 状态代码与对应状态描述
	 * key:状态代码
	 * value:状态描述
	 */
	private static Map<Integer,String> status_code_reason_mapping = new HashMap<Integer,String>(21);;
	
	/**
	 * 介质类型映射
	 * key:资源后缀名
	 * value:介质类型
	 */
	private static Map<String,String> mime_mapping = new HashMap<String, String>();
	
	//静态块最先被加载,比构造器前.
	//构造器需要new以后才被加载,而静态块不需要
	static{
		initStatusMapping();
		initMimeMapping();
	}
	
	/**
	 * 加载保存服务器状态码404,200
	 * key: 200
	 * value: OK
	 */
	private static void initStatusMapping(){
		status_code_reason_mapping.put(200, "OK");
		status_code_reason_mapping.put(201, "Created");
		status_code_reason_mapping.put(202, "Accepted");
		status_code_reason_mapping.put(204, "No Content");
		status_code_reason_mapping.put(301, "Moved Permanently");
		status_code_reason_mapping.put(302, "Moved Temporarily");
		status_code_reason_mapping.put(304, "Not Modified");
		status_code_reason_mapping.put(400, "Bad Request");
		status_code_reason_mapping.put(401, "Unauthorized");
		status_code_reason_mapping.put(403, "Forbidden");
		status_code_reason_mapping.put(404, "Not Found");
		status_code_reason_mapping.put(500, "Internal Server Error");
		status_code_reason_mapping.put(501, "Not Implemented");
		status_code_reason_mapping.put(502, "Bad Gateway");
		status_code_reason_mapping.put(503, "Service Unavailable");
	}
	
	
	
	/**
	 * 加载保存服务器介质信息
	 * key: html
	 * value: text/html
	 */
	private static void initMimeMapping(){
		
		try {
			
			//1.创建读取方法
			SAXReader reader = new SAXReader();
			
			//2.读取xml文件
			Document doc = reader.read(new FileInputStream("conf/web.xml"));
			
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
				mime_mapping.put(extension, mime_type);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 根据 资源后缀名 获取 介质类型
	 */
	public static String getMimeType(String ext){
		return mime_mapping.get(ext);
	}
	
	
	/**
	 * 根据 状态码 获取 状态描述
	 */
	public static String getStatusReason(int code){
		return status_code_reason_mapping.get(code);
	}
	
}
