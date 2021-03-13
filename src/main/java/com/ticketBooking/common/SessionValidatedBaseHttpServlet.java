package com.ticketBooking.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionValidatedBaseHttpServlet extends BaseHttpServlet {

	@Override
	protected void onRequestReceived(HttpServletRequest request) throws UnauthorizedAccessException {
		super.onRequestReceived(request);

		// String JSESSIONID = null;

		HttpSession session = request.getSession(false);
		if (session == null) {
			throw new UnauthorizedAccessException("Unauthorized access denied");
		}

		/*
		 * Cookie[] cookies = request.getCookies(); for (Cookie cookie : cookies) { if
		 * ("JSESSIONID".equals(cookie.getName())) { JSESSIONID = cookie.getValue(); } }
		 * if (JSESSIONID == null) { throw new
		 * UnauthorizedAccessException("Unauthorized access denied"); }
		 */

	}

}
