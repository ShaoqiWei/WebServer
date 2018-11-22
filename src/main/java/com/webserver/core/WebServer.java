package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer主类
 *
 */
public class WebServer {
	private ServerSocket server;
	
	/**
	 * 线程池
	 * newCachedThreadPool
	 * 		创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。 
	 * 	 	线程池为无限大，当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程。
	 * newFixedThreadPool 
	 * 		创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。 
	 * 		线程池为无限大，当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程。
	 * newScheduledThreadPool 
	 *  	创建一个定长线程池，支持定时及周期性任务执行。 
	 * newSingleThreadExecutor 
	 * 		创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
	 */
	private ExecutorService threadPool;
	
	/**
	 * 构造方法，用来初始化服务端
	 */
	public WebServer() {
		try {
			System.out.println("正在启动服务端...");
			server = new ServerSocket(8088);
			
			//这个线程池最多50个
			threadPool = Executors.newFixedThreadPool(50);
			System.out.println("服务端启动完毕!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 服务端开始工作的方法
	 */
	public void start() {
		try {
			/*
			 * 暂时只处理客户端的一次请求
			 */
			System.out.println("等待客户端连接...");
			while(true) {
				Socket socket = server.accept();
				System.out.println("一个客户端连接了！");
			
				//启动一个线程处理该客户端请求
				ClientHandler handler = new ClientHandler(socket);
				
				//将当前线程放入线程池中
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


