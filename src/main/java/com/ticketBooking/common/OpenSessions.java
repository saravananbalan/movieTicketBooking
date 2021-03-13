package com.ticketBooking.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

public class OpenSessions {

	private static OpenSessions single_instance = null;
	private Map<String, HttpSession> openClientSessions = null;

	public static OpenSessions getInstance() {
		if (single_instance == null)
			single_instance = new OpenSessions();

		return single_instance;
	}

	private OpenSessions() {
		openClientSessions = new HashMap<String, HttpSession>();
	}

	public void addToOpenClientSessions(String id, HttpSession session) {
		openClientSessions.put(id, session);
	}

	public HttpSession getOpenClientSessionById(String sessionId) {
		return openClientSessions.get(sessionId);
	}

	public void removeFromOpenClientSessions(String sessionId) {
		openClientSessions.remove(sessionId);
	}

}
