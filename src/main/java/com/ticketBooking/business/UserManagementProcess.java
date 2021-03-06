package com.ticketBooking.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.uuid.Generators;
import com.ticketBooking.common.DBConnection;

public class UserManagementProcess {

	public String addDetails(JSONObject requestObject, JSONObject dataObject) {

		String query = "INSERT INTO dashboard_details (cinemahall,shows,movies,no_of_seats,available_seats,booked_seats,"
				+ "hall_id,ticket_rate,cgst,sgst,movie_id) values (?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBConnection.getInstance().getCon();
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, requestObject.getString("cinemaHall"));
			pstmt.setString(2, requestObject.getString("shows"));
			pstmt.setString(3, requestObject.getString("movies"));
			pstmt.setInt(4, requestObject.getInt("numberOfSeats"));
			pstmt.setInt(5, requestObject.getInt("numberOfSeats"));
			pstmt.setInt(6, 0);
			String hallId = Generators.timeBasedGenerator().generate().toString();
			String movieId = Generators.timeBasedGenerator().generate().toString();
			pstmt.setString(7, hallId);
			pstmt.setInt(8, requestObject.optInt("ticketRate"));
			pstmt.setInt(9, requestObject.optInt("gst"));
			pstmt.setInt(10, requestObject.optInt("gst"));
			pstmt.setString(11, movieId);

			pstmt.execute();

			insertShowDetails(conn, hallId, movieId, requestObject.getJSONArray("showTime"));

		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeStatement(pstmt);
			DBConnection.closeConnection(conn);
		}
		return null;

	}

	private boolean insertShowDetails(Connection conn, String hallId, String movieId, JSONArray showTimes) {

		String query = "INSERT INTO show_seat_details (hall_id,movieid,show_id,show_time,seat_numbers) values (?,?,?,?,?)";
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(query);
			for (int i = 0; i < showTimes.length(); i++) {
				pstmt.setString(1, hallId);
				pstmt.setString(2, movieId);
				pstmt.setString(3, Generators.timeBasedGenerator().generate().toString());
				pstmt.setString(4, showTimes.getString(i));
				pstmt.setString(5, "[]");

				pstmt.execute();
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DBConnection.closeStatement(pstmt);

		}
	}

	public String updateDetails(JSONObject requestObject, JSONObject dataObject) throws JSONException, SQLException {

		String UPDATE_CONSTANTS_QUERY = "update dashboard_details set cinemahall =?,shows=?,movies=?,no_of_seats=?,ticket_rate=?,cgst=?,sgst=?"
				+ " where id = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBConnection.getInstance().getCon();
			pstmt = conn.prepareStatement(UPDATE_CONSTANTS_QUERY);

			pstmt.setString(1, requestObject.optString("cinemahall"));
			pstmt.setString(2, requestObject.optString("shows"));
			pstmt.setString(3, requestObject.optString("movies"));
			pstmt.setInt(4, requestObject.optInt("numberOfSeats"));
			pstmt.setInt(5, requestObject.optInt("ticketRate"));
			pstmt.setInt(6, requestObject.optInt("gst"));
			pstmt.setInt(7, requestObject.optInt("gst"));
			pstmt.setString(8, requestObject.getString("id"));

			pstmt.execute();
		} finally {
			DBConnection.closeStatement(pstmt);
			DBConnection.closeConnection(conn);
		}
		return null;
	}

	public String getDetails(JSONObject requestObject, JSONObject dataObject) throws JSONException, SQLException {

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
				dashboardDetailsObject.put("numberOfSeats", rs.getInt("no_of_seats"));
				dashboardDetailsObject.put("available_seats", rs.getInt("available_seats"));
				dashboardDetailsObject.put("booked_seats", rs.getInt("booked_seats"));
				dashboardDetailsObject.put("ticket_rate", rs.getInt("ticket_rate"));
				dashboardDetailsObject.put("gst", rs.getInt("cgst"));

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

	public String deleteDetails(JSONObject requestObject, JSONObject dataObject) throws SQLException {
		String DELETE_CONSTANT_QUERY = "delete from dashboard_details where id = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBConnection.getInstance().getCon();

			pstmt = conn.prepareStatement(DELETE_CONSTANT_QUERY);
			pstmt.setString(1, requestObject.getString("id"));
			pstmt.execute();
		} finally {
			DBConnection.closeStatement(pstmt);
			DBConnection.closeConnection(conn);
		}
		return null;
	}

}
