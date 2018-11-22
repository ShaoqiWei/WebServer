package com.webserver.http;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * HttpЭ��������ݶ���
 * 
 * @author ASUS
 *
 */
public class HttpContext {
	
	/**
	 * �س���CR
	 */
	public static final int CR = 13;
	/**
	 * ���з�LF
	 */
	public static final int LF = 10;
	
	
	/**
	 * ״̬�������Ӧ״̬����
	 * key:״̬����
	 * value:״̬����
	 */
	private static Map<Integer,String> status_code_reason_mapping = new HashMap<Integer,String>(21);;
	
	/**
	 * ��������ӳ��
	 * key:��Դ��׺��
	 * value:��������
	 */
	private static Map<String,String> mime_mapping = new HashMap<String, String>();
	
	//��̬�����ȱ�����,�ȹ�����ǰ.
	//��������Ҫnew�Ժ�ű�����,����̬�鲻��Ҫ
	static{
		initStatusMapping();
		initMimeMapping();
	}
	
	/**
	 * ���ر��������״̬��404,200
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
	 * ���ر��������������Ϣ
	 * key: html
	 * value: text/html
	 */
	private static void initMimeMapping(){
		
		try {
			
			//1.������ȡ����
			SAXReader reader = new SAXReader();
			
			//2.��ȡxml�ļ�
			Document doc = reader.read(new FileInputStream("conf/web.xml"));
			
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
				mime_mapping.put(extension, mime_type);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * ���� ��Դ��׺�� ��ȡ ��������
	 */
	public static String getMimeType(String ext){
		return mime_mapping.get(ext);
	}
	
	
	/**
	 * ���� ״̬�� ��ȡ ״̬����
	 */
	public static String getStatusReason(int code){
		return status_code_reason_mapping.get(code);
	}
	
}
