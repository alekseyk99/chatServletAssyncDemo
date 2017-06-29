package com.alekseyk99;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class ServiceTest {

	@Test
	public void testMessages() {

		ChatService service = new ChatService();
		int id= service.addMessage("test1");

	        assertEquals("should be 1", 1, id);
	        
	        id= service.addMessage("test2");
	        assertEquals("should be 1", 2, id);
	        
	        List<String> list = service.findLatestMessages(1);
	        
	        assertEquals("should be 1", 1, list.size());

	        assertEquals("should be 'test2'", "test2", list.get(0));

	    }
	}

