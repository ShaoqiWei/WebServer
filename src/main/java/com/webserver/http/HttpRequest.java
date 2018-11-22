package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.webserver.core.EmptyRequestException;

/**
 * 获取客户端发送过来的请求
 * @author ASUS
 *
 */
public class HttpRequest {

	/**
	 * 请求行相关信息定义
	 * merhod:请求方式
	 * url:资源路径
	 * protocol:协议版本
	 */
	private String merhod;
	private String url;
	private String protocol;
	
	/**
	 * url中的请求部分
	 * requsetURI:url中请求部分
	 * queryString:请求内容
	 * queryString:请求参数
	 */
	private String requsetURI;
	private String queryString;
	private Map<String,String> parameters = new HashMap<String,String>();
	
	
	/**
	 * 消息头相关消息定义
	 */
	private Map<String,String> headers = new HashMap<String,String>();
	
	
	/**
	 * 消息正文相关消息定义
	 */
	
	
	
	private InputStream in;
	
	/**
	 * 初始化请求信息
	 * @throws EmptyRequestException 
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException{
		try {
			this.in = socket.getInputStream();
			
			/*
			 * 解析请求
			 * 1:解析请求行
			 * 2:解析消息头
			 * 3:解析消息正文
			 */
			parseRequestLine();
			parseHeaders();
			parseContent();
			
		} catch(EmptyRequestException e){
			//构造器将空指针异常抛给调用者
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 第一个方法:解析请求行
	 * 以下方法可能会报下标越界的错,原因是HTTP协议允许客户端发送一个空请求过来
	 * @throws EmptyRequestException 
	 */
	private void parseRequestLine() throws EmptyRequestException{
		
		try {
			String line = readLine();
			/*
			 * 将请求行进行拆分,赋值到对应属性上
			 */
			String[] data = line.split("\\s");
			if(data.length != 3){
				//将数组越界异常抛给调用者(本例子是先抛给构造器)
				throw new EmptyRequestException();
			}
			
			this.merhod = data[0];
			this.url = data[1];
			//进一步解析URL,判断是资源请求还是逻辑请求
			parseURL();
			this.protocol = data[2];
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * 进一步解析URL
	 * url有可能会有两种格式:带参数和不带参数
	 * 1:不带参数如下:
	 * /myweb/reg.html
	 * 2:带参数
	 * /myweb/reg?username=1password=2&nickname=3&age=4
	 * @throws EmptyRequestException 
	 */
	private void parseURL(){
		/*
		 * 判断当前URL是否有参数
		 * 	(有？代表有参数)
		 * 有的话解析参数,添加到Map parameters中
		 * 没有的话直接将url赋值给requsetURI
		 * 
		 * URL可能有以下四种种情况
		 * /myweb/reg.html
		 * /myweb/reg?username=1&password=2&nickname=3&age=4
		 * /myweb/reg?username=&password=&nickname=&age=
		 * /myweb/reg?
		 * 
		 */
		
		//1.判断URL中如果有"？"且不在结尾,开始拆分
		if(this.url.contains("?") && !this.url.endsWith("?")){
			
			//2.以？拆分
			String[] data = url.split("\\?");
			this.requsetURI = data[0];
			this.queryString = data[1];
			parseParameter(queryString);
			
		}else{
			this.requsetURI = this.url;
		}
		
		System.out.println("requsetURI==>"+requsetURI);
		System.out.println("queryString==>"+queryString);
		System.out.println("parameters==>"+parameters);
		
	}
	
	
	/**
	 * 第二个方法:解析消息头
	 * 循环调用readline()方法,读取每一个消息头
	 * 当readline()返回空字符串时停止循环
	 * 将读取到的消息按照": "拆分,消息头存为key,消息体存为对应的value,存入headers
	 */
	private void parseHeaders(){
		System.out.println("2.开始解析消息头...");
		String str = "";
		String[] strs;
		try {
			while(!(str = readLine()).equals("")){
				strs = str.split(":\\s");
				headers.put(strs[0], strs[1]);
				System.out.println(strs[0]+"==="+strs[1]);
			}
		} catch (Exception e) {  
			e.printStackTrace();
		}
		
		System.out.println("消息头解析完毕");
	}
	
	/**
	 * 第三个方法:解析消息正文
	 */
	private void parseContent(){
		System.out.println("3.开始解析消息正文...");
	
		try {
			
			/*
			 *根据消息头是否含有Content-Length决定是否含有消息正文 
			 */
			if(headers.containsKey("Content-Length")){
				int length = Integer.parseInt(headers.get("Content-Length"));
				
				//读取消息正文
				byte[] data = new byte[length];
				in.read(data);
				
				//更根据消息头Content-Type判断该消息正文的数据类型
				String contentType = headers.get("Content-Type");
				System.out.println("Content-Type的数据类型:"+contentType);
				if(contentType.equals("application/x-www-form-urlencoded")){
					String line = new String(data,"ISO8859-1");
					System.out.println("消息正文:"+line);
					parseParameter(line);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("消息正文解析完毕");
	}
	
	/**
	 * 解析参数
	 * 格式:name=value&name=value&...
	 * @param line
	 */
	private void parseParameter(String line) {
		/*
		 * 现将参数中的"%XX"的内容按照对应
		 * 字符集(浏览器通常用UTF-8)还原为
		 * 对应文字
		 */
		try {
			/*
			 * URLDecoder的decode方法可以将给定的
			 * 字符串中的"%XX"内容转为对应2进制字节
			 * 然后按照给定的字符集将这些字节还原
			 * 为对应字符并替换这些"%XX"部分，然后
			 * 将换好的字符串返回
			 * 比如line的内容为:
			 * username=%E8%8C%83%E4%BC%A0%E5%A5%87&password=123456
			 * 转码完毕后为:
			 * username=范传奇&password=123456
			 * 
			 */
			System.out.println("对参数转码前:"+line);
			line = URLDecoder.decode(line, "UTF-8");
			System.out.println("对参数转码后:"+line);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		
		//按照&拆分出每一个参数
		String[] paraArr = line.split("&");
		//遍历每个参数进行拆分
		for(String para : paraArr) {
			//再按照"="拆分每个参数
			String[] paras = para.split("=");
			if(paras.length>1) {
				//该参数有值
				parameters.put(paras[0], paras[1]);
			}else {
				//没有值
				parameters.put(paras[0], null);
			}
		}
	}
	
	
	/**
	 * 读取请求字符串的一行,用于解析
	 */
	public String readLine() throws IOException{
		
		StringBuilder builder = new StringBuilder();
		//本次读到的字节
		int d = -1;
		//c1是上次读到的字符,c2是本次读到的字符
		char c1 = 'a';
		while((d = in.read()) != -1){
			c1 = (char)d;
			if(c1==13){
				if((d = in.read())==10)
					break;
				else{
					builder.append((char)d);
					continue;
				}
			}
			builder.append(c1);
		}
		return builder.toString();
	}


	public String getMerhod() {
		return merhod;
	}


	public String getUrl() {
		return url;
	}


	public String getProtocol() {
		return protocol;
	}


	public String getRequsetURI() {
		return requsetURI;
	}


	public String getQueryString() {
		return queryString;
	}
	
	/**
	 * 根据parameters 参数的name获得参数的value值
	 * @param name
	 * @return
	 */
	public String getParameter(String name){
		return parameters.get(name);
	}
	
}
