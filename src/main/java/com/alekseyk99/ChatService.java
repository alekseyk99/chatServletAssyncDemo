package com.alekseyk99;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Service layer
 * 
 *
 */
public class ChatService {

	// cached ID of latest data
	private AtomicInteger latestId = new AtomicInteger(0);
	
	List<String> messages = Collections.synchronizedList(new ArrayList<String>());
	
	/**
	 * Add message to repository, update internal id
	 * 
	 * @param message
	 * @return ID of created message
	 */
	public int addMessage(String message) {
		int id;
		// needed synchronization because its two independent operations
		synchronized(messages) {
			messages.add(message);
			id = messages.size();
		}
		// update latestId only if (id > latestId) 
		int v;
		do {
			v = latestId.get();
		} while ((id > v)&&(!latestId.compareAndSet(v, id)));
		return id;
	};
	
	/**
	 * Find messages whose ID is greater that provided id 
	 * @param id
	 * @return List<String> or NULL 
	 */
	public List<String> findLatestMessages(int id) {
		
		int currentId = latestId.get();

		// looking for new messages
		if (id < currentId) {
			return new ArrayList<String>(messages.subList(id, currentId));
		} else {
			// or could return empty list
			return null;
		}
	}
	
	/**
	 * Check if repository has more recent data
	 * 
	 * @param id
	 * @return "true" if there is newest data
	 */
	public boolean hasNewMessages(int id) {
		return (id < latestId.get());
	}	
	
}
