package com.ticketBooking.common;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class BaseHttpServlet extends HttpServlet {

	protected JSONObject responseObject;
	protected Logger log;

	public BaseHttpServlet() {
	}

	// ========================================================

	protected void onRequestReceived(HttpServletRequest request) throws UnauthorizedAccessException {
		responseObject = new JSONObject();
	}

	protected void sendResponse(HttpServletResponse response) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		String responseString = null;
		try {
			if (CONSTANTS.encryptionEnabled) {
				responseString = Encryption.encryptText("InputRequirmntTI", responseObject.toString());
				response.getWriter().print(responseString);
			} else {
				response.getWriter().print(responseObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void addToCookie(HttpServletResponse response, String key, String value) {

		Cookie cookie = new Cookie(key, value);
		response.addCookie(cookie);
	}

	// ========================================================

	protected void setResponseStatus(boolean status) {
		responseObject.put("status", status);
	}

	protected void setResponseErrorMessage(String message) {
		responseObject.put("message", message);
	}

	protected void setResponseData(String key, Object value) {
		responseObject.put(key, value);
	}

	protected String getRequestBody(HttpServletRequest request) throws Exception {

		String Content_type = request.getContentType();
		String requestBody = "";
		if (Content_type.equalsIgnoreCase("application/json")) {

			StringBuffer jb = new StringBuffer();

			try {
				BufferedReader reader = request.getReader();

				char[] chars = new char[4 * 1024];
				int len;
				while ((len = reader.read(chars)) >= 0) {
					jb.append(chars, 0, len);
				}

			} catch (Exception e) {
			}
			requestBody = jb.toString();
		} else {
			throw new Exception("Only Content-type json is supported");
		}
		if (CONSTANTS.encryptionEnabled) {
			return Encryption.decryptText("InputRequirmntTI", requestBody);
		} else {
			return requestBody;
		}

	}

}
