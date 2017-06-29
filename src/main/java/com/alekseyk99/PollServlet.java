package com.alekseyk99;

import java.io.IOException;
import java.io.PrintWriter;
//import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * servlet provides long polling 
 *
 */
@WebServlet(name="pollServlet", urlPatterns={"/poll"}, asyncSupported=true)
public class PollServlet extends HttpServlet {

		private static final long serialVersionUID = 2855492151632938252L;
		
		/**
		 * handle post requests
		 * @param id Id of last received message
		 */
		@SuppressWarnings("unchecked")
		@Override
	    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
			String idString = request.getParameter("id");
			System.out.println("servlet /poll id="+idString);
			int id = Integer.parseInt(idString);
			ServletContext context = request.getServletContext();
			ChatService service = (ChatService) context.getAttribute("service");
			final Queue<AsyncContext> watchers = (Queue<AsyncContext>) context.getAttribute("watchers");

			// looking for new messages
		    List<String> newMessages = service.findLatestMessages(id);
		    if (newMessages == null) {
		    	// Activate long polling
		    	System.out.println("Long polling");
			AsyncContext async = request.startAsync();
			async.setTimeout(30000); // 30 seconds
			async.addListener(new AsyncListener() {
			    	public void onTimeout(AsyncEvent event) throws IOException {
						System.out.println("Timeout");
						AsyncContext asyncContext = event.getAsyncContext();
						try {
							asyncContext.complete();
						} catch( IllegalStateException ex) {
							// if already completed
							System.out.println("onTimeout: IllegalStateException " + ex.getMessage());
						}
						watchers.remove(asyncContext);
					}
				public void onComplete(AsyncEvent event) throws IOException {}
				public void onError(AsyncEvent event) throws IOException {}
				public void onStartAsync(AsyncEvent event) throws IOException {}
			});
			    
		    	watchers.offer(async);
		    	
		    	// new messages could arrived before we add watcher
			    if (service.hasNewMessages(id)) { 
			    	System.out.println("Rare ambiguous situation: new messages arrived, it could be before we add watcher");
			    	// force to update polling
			    	if (watchers.remove(async)) {
			    		// our async was still in query
					try {
						async.dispatch();
					} catch( IllegalStateException ex) {
						// if already completed
						System.out.println("IllegalStateException--" + ex.getMessage());
					} 
			    	};
			    }
		    } else {
		    	//send new messages and close connection, no need for long polling
		    	PrintWriter writer = response.getWriter();
		    	writer.print("[");
		    	for (int i=0;i<newMessages.size();i++) {
		    		if (i!=0) { writer.print(","); }
		    		writer.print("{\"id\": \"");
		    		writer.print(id+i+1);
		    		writer.print("\", \"message\": \"");
		    		writer.print(newMessages.get(i));
		    		writer.print("\"}");
		    	}
		    	writer.print("]");
		    }
	   }

}
