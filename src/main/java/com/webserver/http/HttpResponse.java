package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * ��Ӧ�ͻ���:�����ͻ��������,�����������Ӧ
 * 
 * һ����Ӧ����:
 * 	״̬��,��Ӧͷ����Ӧ����
 * @author ASUS
 *
 */
public class HttpResponse {
	/**
	 * ״̬�������Ϣ����
	 * statusCode:״̬����
	 * statusReason:״̬����
	 */
	private int statusCode = 200;
	private String statusReason = "OK";
	
	
	/**
	 * ��Ӧͷ�����Ϣ����
	 * key������Ӧͷ����
	 * value�����Ӧ��ֵ
	 */
	private Map<String,String> headers = new HashMap<String,String>();
	
	
	/**
	 * ��Ӧ���������Ϣ����
	 */
	//��Ӧʵ���ļ�
	private File entity;
	
	
	//������������Զ���
	private Socket socket;
	private OutputStream out;
	
	public HttpResponse(Socket socket){
		try {
			
			this.socket = socket;
			this.out = socket.getOutputStream();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����ǰ��Ӧ���ݷ��͸��ͻ���
	 */
	public void flush(){
		/*
		 * ��Ӧ�ͻ���:
		 * 1:����״̬��
		 * 2:������Ӧͷ
		 * 3:������Ӧ����
		 */
		try {
			sendStatusLine();
			sendHeaders();
			sendContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * ����״̬��
	 * @throws IOException 
	 */
	private void sendStatusLine() throws IOException{
		try {
			
			//��Ӧ�ͻ��˵���Դ
			this.out = socket.getOutputStream();
			
			//����״̬��
			String line = "HTTP/1.1" + " " + statusCode + " " + statusReason;
			println(line);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ������Ӧͷ
	 */
	private void sendHeaders(){
		try {
			//����headers,��������Ӧͷ����
			Set<Entry<String,String>> set = headers.entrySet();
			StringBuilder line = new StringBuilder();
			for(Entry<String,String> header : set){
				
				line.append(header.getKey());
				line.append(": ");
				line.append(header.getValue());
				
				println(line.toString());
				System.out.println("line:"+line);
				line.delete(0, line.length());
			}
			
			//����CRLF���ͱ�ʾ��Ӧͷ����
			println("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ������Ӧ����
	 */
	private void sendContent(){
		try(FileInputStream fis = new FileInputStream(entity);)
		{
			//������Ӧ����
			byte[] data = new byte[1024*10];
			int len = -1;
			while((len = fis.read(data)) != -1){
				out.write(data, 0, len);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			//��ͻ��˶Ͽ�����
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public File getEntity() {
		return entity;
	}

	/**
	 * 1:������Ӧʵ���ļ�
	 * 2:�Զ���Ӷ�Ӧ��Content-Type��Content-Lenght
	 * @param entity
	 */
	public void setEntity(File entity) {
		this.entity = entity;
		
		//��ȡ�ļ�����,�Զ���Ӷ�Ӧ��Content-Type��Content-Lenght
		this.headers.put("Content-Lenght", entity.length()+"");
		
		/*
		 * html     text/html
		 * png      image/png
		 * gif      image/gif
		 *  
		 */
		String fileName = this.entity.getName();
		fileName = fileName.substring(fileName.lastIndexOf('.')+1);
		
		this.headers.put("Content-Type", HttpContext.getMimeType(fileName));
		
	}

	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * ����״̬������Զ�����״̬����
	 * @param statusCode
	 */
	public void setStatusCode(int statusCode) {
		
		this.statusCode = statusCode;
		this.statusReason = HttpContext.getStatusReason(statusCode);
	}

	public String getStatusReason() {
		return statusReason;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
	
	/**
	 * ���ָ������Ӧͷ��Ϣ
	 * @param name ��Ӧͷ������
	 * @param value ��Ӧͷ��Ӧ��ֵ
	 */
	public void putHeader(String name,String value) {
		this.headers.put(name, value);
	}
	/**
	 * ��ͻ��˷���һ���ַ���
	 * ���ͺ���Զ�����CR,LF
	 * @param line
	 */
	private void println(String line) {
		try {
			this.out.write(line.getBytes("ISO8859-1"));
			this.out.write(HttpContext.CR);//written CR
			this.out.write(HttpContext.LF);//written LF
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}


 