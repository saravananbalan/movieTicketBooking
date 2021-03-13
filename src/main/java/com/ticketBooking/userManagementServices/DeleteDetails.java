package com.ticketBooking.userManagementServices;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.ticketBooking.business.UserManagementProcess;
import com.ticketBooking.common.BaseHttpServlet;
import com.ticketBooking.common.UnauthorizedAccessException;

public class DeleteDetails extends BaseHttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			onRequestReceived(request);
			JSONObject requestObject = new JSONObject(getRequestBody(request));
			JSONObject dataObject = new JSONObject();

			String result = new UserManagementProcess().deleteDetails(requestObject, dataObject);
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
