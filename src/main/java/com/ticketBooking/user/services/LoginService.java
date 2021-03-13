package com.ticketBooking.user.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.ticketBooking.business.UserProcess;
import com.ticketBooking.common.BaseHttpServlet;
import com.ticketBooking.common.UnauthorizedAccessException;

public class LoginService extends BaseHttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			onRequestReceived(request);
			HttpSession session = request.getSession(true);
			session.setMaxInactiveInterval(30 * 60);

			JSONObject requestObject = new JSONObject(getRequestBody(request));
			JSONObject dataObject = new JSONObject();

			String result = new UserProcess().authenticateUser(requestObject, dataObject);
			if (result == null) {
				setResponseStatus(true);
				setResponseData("data", dataObject);
			} else {
				setResponseStatus(false);
			}
		} catch (UnauthorizedAccessException e) {
			setResponseStatus(false);
			setResponseErrorMessage(e.getMessage());
		} catch (Exception e) {
			setResponseStatus(false);
			setResponseErrorMessage("Technical error");
		}
		sendResponse(response);

	}

}
