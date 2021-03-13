package com.ticketBooking.bookTickets.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.ticketBooking.business.TicketBookingProcess;
import com.ticketBooking.common.BaseHttpServlet;
import com.ticketBooking.common.SessionValidatedBaseHttpServlet;
import com.ticketBooking.common.UnauthorizedAccessException;

public class BookTicket extends SessionValidatedBaseHttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			HttpSession session = request.getSession(false);
			onRequestReceived(request);
			JSONObject requestObject = new JSONObject(getRequestBody(request));
			JSONObject dataObject = new JSONObject();

			String result = new TicketBookingProcess().bookTickets(requestObject, dataObject, session);
			if (result == null) {
				setResponseStatus(true);
				setResponseData("data", dataObject);
			} else {
				setResponseData("data", result);
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
