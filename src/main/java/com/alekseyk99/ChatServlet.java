package com.alekseyk99;

import java.io.IOException;
import java.util.Queue;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet receives new messages 
 *
 */
@WebServlet(name="chatServlet", urlPatterns={"/chat"}, asyncSupported=false)
public class ChatServlet extends HttpServlet {

		private static final long serialVersionUID = 2855492151632938252L;
	
		/**
		 * Handle post requests
		 * 
		 * @param message New message
		 */
		@SuppressWarnings("unchecked")
		@Override
	    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
			System.out.println("chatServlet post");
	
			String message = request.getParameter("message");
			ServletContext context = request.getServletContext();
			ChatService service = (ChatService) context.getAttribute("service");
			Queue<AsyncContext> watchers = (Queue<AsyncContext>) context.getAttribute("watchers");
	    	
			service.addMessage(message);
			// propagate message to all waiting polling requests
			AsyncContext watcher;
			while((watcher = watchers.poll())!=null) {
				try {
					watcher.dispatch();
				} catch( IllegalStateException ex) {
					// if already completed
					System.out.println("chatServlet: IllegalStateException " + ex.getMessage());
				} 
			}
	   }

}
