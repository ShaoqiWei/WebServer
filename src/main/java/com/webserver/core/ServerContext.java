package com.webserver.core;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * ���ر�������·���뷴������Ϣ
 * key: /myweb/login
 * value: com.webserver.servlets.LoginServlet
 */
public class ServerContext {
	
	private static Map<String,String> servletMapping = new HashMap<String, String>();
	
	static{
		initServletMapping();
	}
	
	/**
	 * ���ر�������·���뷴������Ϣ
	 * key: /myweb/login
	 * value: com.webserver.servlets.LoginServlet
	 */
	private static void initServletMapping(){

		try {
			
			//1.������ȡ����
			SAXReader reader = new SAXReader();
			
			//2.��ȡxml�ļ�
			Document doc = reader.read(new FileInputStream("conf/Servlets.xml"));
			
			//3.��ȡ���ڵ�
			Element root = doc.getRootElement();
			
			//4��ȡ����ǩ�µ�����Ա����ǩ<mime-mapping>��ʵ��
			@SuppressWarnings("unchecked")
			List<Element> mime = root.elements("mime-mapping");

			/*
			 * ȡ��ÿ��<mime-mapping>����Ϣ
			 */
			for(Element emp:mime){
				//��ȡ�ӱ�ǩ������
				String extension = emp.elementText("extension");
				String mime_type = emp.elementText("mime-type");
				servletMapping.put(extension, mime_type);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * ��������·����ȡ��ӦServlet��������
	 * @param url
	 * @return Servlet Class Name
	 */
	public static String getServletName(String url){
		return servletMapping.get(url);
	}
}
