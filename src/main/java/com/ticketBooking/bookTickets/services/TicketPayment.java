package com.ticketBooking.bookTickets.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.ticketBooking.business.PaymentProcess;

import com.ticketBooking.common.SessionValidatedBaseHttpServlet;
import com.ticketBooking.common.UnauthorizedAccessException;

public class TicketPayment extends SessionValidatedBaseHttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {

			onRequestReceived(request);
			JSONObject requestObject = new JSONObject(getRequestBody(request));
			JSONObject dataObject = new JSONObject();

			String result = new PaymentProcess().payment(requestObject, dataObject);
			if (result == null) {
				setResponseStatus(true);
				setResponseData("data", dataObject);
			} else {
				setResponseStatus(false);
				setResponseData("data", result);
			}
		} catch (UnauthorizedAccessException e) {
			setResponseStatus(false);
			setResponseErrorMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			setResponseStatus(false);
			setResponseErrorMessage("Technical error");
		}
		sendResponse(response);
	}

}
