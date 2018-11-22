package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer����
 *
 */
public class WebServer {
	private ServerSocket server;
	
	/**
	 * �̳߳�
	 * newCachedThreadPool
	 * 		����һ���ɻ����̳߳أ�����̳߳س��ȳ���������Ҫ���������տ����̣߳����޿ɻ��գ����½��̡߳� 
	 * 	 	�̳߳�Ϊ���޴󣬵�ִ�еڶ�������ʱ��һ�������Ѿ���ɣ��Ḵ��ִ�е�һ��������̣߳�������ÿ���½��̡߳�
	 * newFixedThreadPool 
	 * 		����һ�������̳߳أ��ɿ����߳���󲢷������������̻߳��ڶ����еȴ��� 
	 * 		�̳߳�Ϊ���޴󣬵�ִ�еڶ�������ʱ��һ�������Ѿ���ɣ��Ḵ��ִ�е�һ��������̣߳�������ÿ���½��̡߳�
	 * newScheduledThreadPool 
	 *  	����һ�������̳߳أ�֧�ֶ�ʱ������������ִ�С� 
	 * newSingleThreadExecutor 
	 * 		����һ�����̻߳����̳߳أ���ֻ����Ψһ�Ĺ����߳���ִ�����񣬱�֤����������ָ��˳��(FIFO, LIFO, ���ȼ�)ִ�С�
	 */
	private ExecutorService threadPool;
	
	/**
	 * ���췽����������ʼ�������
	 */
	public WebServer() {
		try {
			System.out.println("�������������...");
			server = new ServerSocket(8088);
			
			//����̳߳����50��
			threadPool = Executors.newFixedThreadPool(50);
			System.out.println("������������!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ����˿�ʼ�����ķ���
	 */
	public void start() {
		try {
			/*
			 * ��ʱֻ����ͻ��˵�һ������
			 */
			System.out.println("�ȴ��ͻ�������...");
			while(true) {
				Socket socket = server.accept();
				System.out.println("һ���ͻ��������ˣ�");
			
				//����һ���̴߳���ÿͻ�������
				ClientHandler handler = new ClientHandler(socket);
				
				//����ǰ�̷߳����̳߳���
				threadPool.execute(handler);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
}


