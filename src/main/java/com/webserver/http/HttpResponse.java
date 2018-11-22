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
 * 响应客户端:解析客户端请求后,对请求进行响应
 * 
 * 一个响应包含:
 * 	状态行,响应头，响应正文
 * @author ASUS
 *
 */
public class HttpResponse {
	/**
	 * 状态行相关信息定义
	 * statusCode:状态代码
	 * statusReason:状态描述
	 */
	private int statusCode = 200;
	private String statusReason = "OK";
	
	
	/**
	 * 响应头相关信息定义
	 * key保存响应头名字
	 * value保存对应的值
	 */
	private Map<String,String> headers = new HashMap<String,String>();
	
	
	/**
	 * 响应正文相关信息定义
	 */
	//响应实体文件
	private File entity;
	
	
	//与连接相关属性定义
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
	 * 将当前响应内容发送给客户端
	 */
	public void flush(){
		/*
		 * 响应客户端:
		 * 1:发送状态行
		 * 2:发送响应头
		 * 3:发送响应正文
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
	 * 发送状态行
	 * @throws IOException 
	 */
	private void sendStatusLine() throws IOException{
		try {
			
			//响应客户端的资源
			this.out = socket.getOutputStream();
			
			//发送状态行
			String line = "HTTP/1.1" + " " + statusCode + " " + statusReason;
			println(line);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 发送响应头
	 */
	private void sendHeaders(){
		try {
			//遍历headers,将所有响应头发送
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
			
			//单独CRLF发送表示响应头结束
			println("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 发送响应正文
	 */
	private void sendContent(){
		try(FileInputStream fis = new FileInputStream(entity);)
		{
			//发送响应正文
			byte[] data = new byte[1024*10];
			int len = -1;
			while((len = fis.read(data)) != -1){
				out.write(data, 0, len);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			//与客户端断开连接
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
	 * 1:设置相应实体文件
	 * 2:自动添加对应的Content-Type与Content-Lenght
	 * @param entity
	 */
	public void setEntity(File entity) {
		this.entity = entity;
		
		//获取文件属性,自动添加对应的Content-Type与Content-Lenght
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
	 * 设置状态代码会自动设置状态描述
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
	 * 添加指定的响应头信息
	 * @param name 响应头的名字
	 * @param value 响应头对应的值
	 */
	public void putHeader(String name,String value) {
		this.headers.put(name, value);
	}
	/**
	 * 向客户端发送一行字符串
	 * 发送后会自动发送CR,LF
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


 