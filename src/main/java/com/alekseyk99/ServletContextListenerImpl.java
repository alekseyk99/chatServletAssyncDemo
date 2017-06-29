package com.alekseyk99;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServletContextListenerImpl implements ServletContextListener{

   public void contextInitialized(ServletContextEvent sce) {   
	   System.out.println("contextInitialized");

	   ServletContext context = sce.getServletContext();
	   // keep messages 
	   context.setAttribute("service", new ChatService());
	   // keep active listeners 
	   context.setAttribute("watchers", new ConcurrentLinkedQueue<AsyncContext>());
   }
    
   public void contextDestroyed(ServletContextEvent sce) {
	   System.out.println("contextDestroyed");
   }
}