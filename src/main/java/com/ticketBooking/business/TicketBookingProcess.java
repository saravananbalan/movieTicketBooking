package com.ticketBooking.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ticketBooking.common.DBConnection;
import com.ticketBooking.common.OpenSessions;

public class TicketBookingProcess {

	public String bookTickets(JSONObject requestObject, JSONObject dataObject, HttpSession session) {

		int seatCount = requestObject.getInt("seatCount");
		String movieId = requestObject.getString("movieId");
		String userId = requestObject.getString("userId");
		String hallId = requestObject.getString("hallId");

		if (seatCount == 7) {
			return "Please Select seat upto 6";
		} else {

			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getCon();
				JSONObject userTicketObject = new JSONObject();

				if (checkUserTotalTicket(conn, movieId, userId, seatCount, userTicketObject, hallId)) {

					session.setAttribute("movieId", movieId);
					session.setAttribute("userId", userId);
					session.setAttribute("seatCount", seatCount);
					session.setAttribute("hallId", hallId);

					String sessionId = session.getId();
					OpenSessions.getInstance().addToOpenClientSessions(sessionId, session);

					dataObject.put("sessionId", sessionId);
					dataObject.put("availableSeatCountForUser", userTicketObject.getInt("availableTicetForUser"));

					getPreBookTicketDetails(conn, movieId, seatCount, dataObject);
				} else {
					return "user ticket limit exceeded for this movie.";
				}
			} catch (SQLException e) {
				return "Technical error";
			} finally {
				DBConnection.closeConnection(conn);
			}
		}
		return null;
	}

	private boolean checkUserTotalTicket(Connection conn, String movieId, String userId, int seatCount,
			JSONObject userTicketObject, String hallId) {

		String SELECT_VARIABLE_QUERY = "select sum(booked_seats) as booked_seats from booking_details where movieid =? and user_id = ? and hall_id = ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean userBookingStatus = false;

		try {

			pstmt = conn.prepareStatement(SELECT_VARIABLE_QUERY);
			pstmt.setString(1, movieId);
			pstmt.setString(2, userId);
			pstmt.setString(3, hallId);

			rs = pstmt.executeQuery();

			if (rs.next()) {

				int bookedSeats = rs.getInt("booked_seats");
				int totalSeats = bookedSeats + seatCount;

				if (totalSeats <= 6) {
					userTicketObject.put("availableTicetForUser", 6 - bookedSeats);
					userBookingStatus = true;
				} else {
					userBookingStatus = false;
				}
			} else {
				userBookingStatus = true;
				userTicketObject.put("availableTicetForUser", 6);
			}

		} catch (SQLException e) {
			userBookingStatus = false;
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closeStatement(pstmt);
		}
		return userBookingStatus;

	}

	public String getAvailableTickets(JSONObject requestObject, JSONObject dataObject) throws SQLException {

		String SELECT_VARIABLE_QUERY = "select * from dashboard_details";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray dashboardDetailsArray = new JSONArray();
		try {
			conn = DBConnection.getInstance().getCon();
			pstmt = conn.prepareStatement(SELECT_VARIABLE_QUERY);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				JSONObject dashboardDetailsObject = new JSONObject();
				dashboardDetailsObject.put("id", rs.getString("id"));
				dashboardDetailsObject.put("cinemaHall", rs.getString("cinemahall"));
				dashboardDetailsObject.put("shows", rs.getString("shows"));
				dashboardDetailsObject.put("movies", rs.getString("movies"));
				dashboardDetailsObject.put("availableSeats", rs.getInt("available_seats"));
				dashboardDetailsObject.put("bookedSeats", rs.getInt("booked_seats"));
				dashboardDetailsObject.put("hallId", rs.getString("hall_id"));

				dashboardDetailsArray.put(dashboardDetailsObject);
			}
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closeStatement(pstmt);
			DBConnection.closeConnection(conn);
		}
		dataObject.put("dashboardList", dashboardDetailsArray);

		return null;
	}

	private double calculateTicketRate(int bookedSeats, int price, int cgst, int sgst) {

		double ticketRate = bookedSeats * price;
		double d = cgst;
		double gst = ticketRate * (d / 100);
		double totalPrice = ticketRate + (gst * 2);

		return totalPrice;
	}

	private void getPreBookTicketDetails(Connection conn, String movieId, int seatCount, JSONObject dataObject)
			throws SQLException {

		String SELECT_VARIABLE_QUERY = "select ticket_rate,cgst,sgst,movies,shows from dashboard_details "
				+ "where id =?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		JSONObject ticketDetailsObject = new JSONObject();
		try {

			pstmt = conn.prepareStatement(SELECT_VARIABLE_QUERY);
			pstmt.setString(1, movieId);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				int price = rs.getInt("ticket_rate");
				int cgst = rs.getInt("cgst");
				int sgst = rs.getInt("sgst");

				ticketDetailsObject.put("movie", rs.getString("movies"));
				ticketDetailsObject.put("show", rs.getString("shows"));
				ticketDetailsObject.put("price", price);
				ticketDetailsObject.put("cgst", cgst);
				ticketDetailsObject.put("sgst", sgst);
				ticketDetailsObject.put("totalPrice", calculateTicketRate(seatCount, price, cgst, sgst));
			}
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closeStatement(pstmt);
		}
		dataObject.put("ticketDetails", ticketDetailsObject);
	}

}
