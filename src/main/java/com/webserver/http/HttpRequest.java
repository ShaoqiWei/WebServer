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
 * ��ȡ�ͻ��˷��͹���������
 * @author ASUS
 *
 */
public class HttpRequest {

	/**
	 * �����������Ϣ����
	 * merhod:����ʽ
	 * url:��Դ·��
	 * protocol:Э��汾
	 */
	private String merhod;
	private String url;
	private String protocol;
	
	/**
	 * url�е����󲿷�
	 * requsetURI:url�����󲿷�
	 * queryString:��������
	 * queryString:�������
	 */
	private String requsetURI;
	private String queryString;
	private Map<String,String> parameters = new HashMap<String,String>();
	
	
	/**
	 * ��Ϣͷ�����Ϣ����
	 */
	private Map<String,String> headers = new HashMap<String,String>();
	
	
	/**
	 * ��Ϣ���������Ϣ����
	 */
	
	
	
	private InputStream in;
	
	/**
	 * ��ʼ��������Ϣ
	 * @throws EmptyRequestException 
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException{
		try {
			this.in = socket.getInputStream();
			
			/*
			 * ��������
			 * 1:����������
			 * 2:������Ϣͷ
			 * 3:������Ϣ����
			 */
			parseRequestLine();
			parseHeaders();
			parseContent();
			
		} catch(EmptyRequestException e){
			//����������ָ���쳣�׸�������
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ��һ������:����������
	 * ���·������ܻᱨ�±�Խ��Ĵ�,ԭ����HTTPЭ������ͻ��˷���һ�����������
	 * @throws EmptyRequestException 
	 */
	private void parseRequestLine() throws EmptyRequestException{
		
		try {
			String line = readLine();
			/*
			 * �������н��в��,��ֵ����Ӧ������
			 */
			String[] data = line.split("\\s");
			if(data.length != 3){
				//������Խ���쳣�׸�������(�����������׸�������)
				throw new EmptyRequestException();
			}
			
			this.merhod = data[0];
			this.url = data[1];
			//��һ������URL,�ж�����Դ�������߼�����
			parseURL();
			this.protocol = data[2];
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * ��һ������URL
	 * url�п��ܻ������ָ�ʽ:�������Ͳ�������
	 * 1:������������:
	 * /myweb/reg.html
	 * 2:������
	 * /myweb/reg?username=1password=2&nickname=3&age=4
	 * @throws EmptyRequestException 
	 */
	private void parseURL(){
		/*
		 * �жϵ�ǰURL�Ƿ��в���
		 * 	(�У������в���)
		 * �еĻ���������,��ӵ�Map parameters��
		 * û�еĻ�ֱ�ӽ�url��ֵ��requsetURI
		 * 
		 * URL�������������������
		 * /myweb/reg.html
		 * /myweb/reg?username=1&password=2&nickname=3&age=4
		 * /myweb/reg?username=&password=&nickname=&age=
		 * /myweb/reg?
		 * 
		 */
		
		//1.�ж�URL�������"��"�Ҳ��ڽ�β,��ʼ���
		if(this.url.contains("?") && !this.url.endsWith("?")){
			
			//2.�ԣ����
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
	 * �ڶ�������:������Ϣͷ
	 * ѭ������readline()����,��ȡÿһ����Ϣͷ
	 * ��readline()���ؿ��ַ���ʱֹͣѭ��
	 * ����ȡ������Ϣ����": "���,��Ϣͷ��Ϊkey,��Ϣ���Ϊ��Ӧ��value,����headers
	 */
	private void parseHeaders(){
		System.out.println("2.��ʼ������Ϣͷ...");
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
		
		System.out.println("��Ϣͷ�������");
	}
	
	/**
	 * ����������:������Ϣ����
	 */
	private void parseContent(){
		System.out.println("3.��ʼ������Ϣ����...");
	
		try {
			
			/*
			 *������Ϣͷ�Ƿ���Content-Length�����Ƿ�����Ϣ���� 
			 */
			if(headers.containsKey("Content-Length")){
				int length = Integer.parseInt(headers.get("Content-Length"));
				
				//��ȡ��Ϣ����
				byte[] data = new byte[length];
				in.read(data);
				
				//��������ϢͷContent-Type�жϸ���Ϣ���ĵ���������
				String contentType = headers.get("Content-Type");
				System.out.println("Content-Type����������:"+contentType);
				if(contentType.equals("application/x-www-form-urlencoded")){
					String line = new String(data,"ISO8859-1");
					System.out.println("��Ϣ����:"+line);
					parseParameter(line);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("��Ϣ���Ľ������");
	}
	
	/**
	 * ��������
	 * ��ʽ:name=value&name=value&...
	 * @param line
	 */
	private void parseParameter(String line) {
		/*
		 * �ֽ������е�"%XX"�����ݰ��ն�Ӧ
		 * �ַ���(�����ͨ����UTF-8)��ԭΪ
		 * ��Ӧ����
		 */
		try {
			/*
			 * URLDecoder��decode�������Խ�������
			 * �ַ����е�"%XX"����תΪ��Ӧ2�����ֽ�
			 * Ȼ���ո������ַ�������Щ�ֽڻ�ԭ
			 * Ϊ��Ӧ�ַ����滻��Щ"%XX"���֣�Ȼ��
			 * �����õ��ַ�������
			 * ����line������Ϊ:
			 * username=%E8%8C%83%E4%BC%A0%E5%A5%87&password=123456
			 * ת����Ϻ�Ϊ:
			 * username=������&password=123456
			 * 
			 */
			System.out.println("�Բ���ת��ǰ:"+line);
			line = URLDecoder.decode(line, "UTF-8");
			System.out.println("�Բ���ת���:"+line);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		
		//����&��ֳ�ÿһ������
		String[] paraArr = line.split("&");
		//����ÿ���������в��
		for(String para : paraArr) {
			//�ٰ���"="���ÿ������
			String[] paras = para.split("=");
			if(paras.length>1) {
				//�ò�����ֵ
				parameters.put(paras[0], paras[1]);
			}else {
				//û��ֵ
				parameters.put(paras[0], null);
			}
		}
	}
	
	
	/**
	 * ��ȡ�����ַ�����һ��,���ڽ���
	 */
	public String readLine() throws IOException{
		
		StringBuilder builder = new StringBuilder();
		//���ζ������ֽ�
		int d = -1;
		//c1���ϴζ������ַ�,c2�Ǳ��ζ������ַ�
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
	 * ����parameters ������name��ò�����valueֵ
	 * @param name
	 * @return
	 */
	public String getParameter(String name){
		return parameters.get(name);
	}
	
}
