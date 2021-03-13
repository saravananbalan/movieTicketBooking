package com.ticketBooking.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import com.fasterxml.uuid.Generators;
import com.ticketBooking.common.DBConnection;
import com.ticketBooking.common.Encryption;

public class UserProcess {

	public String authenticateUser(JSONObject requestObject, JSONObject dataObject) throws SQLException {

		String userName = requestObject.getString("username");
		String password = requestObject.getString("password");

		String SELECT_LOGIN_DETAILS_QUERY = "select username,role,userid from user_profiles where username = ? and password =?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getInstance().getCon();
			pstmt = conn.prepareStatement(SELECT_LOGIN_DETAILS_QUERY);
			pstmt.setString(1, userName);
			pstmt.setString(2, Encryption.encryptText("poppoye@13gteryt", password));
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dataObject.put("userName", rs.getString("username"));
				dataObject.put("role", rs.getString("role"));
				dataObject.put("userId", rs.getString("userid"));
			} else {
				return "User not found";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closeStatement(pstmt);
			DBConnection.closeConnection(conn);
		}
		return null;
	}

	public String signUp(JSONObject requestObject, JSONObject dataObject) {

		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getCon();
			String username = requestObject.getString("username");
			String password = requestObject.getString("password");
			String mobileNumber = requestObject.getString("mobileNumber");
			String emailId = requestObject.optString("emailId");

			if (isUserAvailable(conn, mobileNumber, emailId)) {

				return "user already availabe login";

			} else {

				addNewUser(conn, username, password, mobileNumber, emailId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection(conn);
		}

		return null;
	}

	private void addNewUser(Connection conn, String username, String password, String mobileNumber, String emailId) {

		String query = "INSERT INTO user_profiles (username,password,mobile_number,email_id,userid,role) values (?,?,?,?,?,?)";
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, username);
			pstmt.setString(2, Encryption.encryptText("poppoye@13gteryt", password));
			pstmt.setString(3, mobileNumber);
			pstmt.setString(4, emailId);
			pstmt.setString(5, Generators.timeBasedGenerator().generate().toString());
			pstmt.setString(6, "user");

			pstmt.execute();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			DBConnection.closeStatement(pstmt);
		}

	}

	private boolean isUserAvailable(Connection conn, String mobileNumber, String emailId) throws SQLException {

		String SELECT_VARIABLE_QUERY = "select * from user_profiles where mobile_number=? or email_id =?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = false;

		try {
			pstmt = conn.prepareStatement(SELECT_VARIABLE_QUERY);
			pstmt.setString(1, mobileNumber);
			pstmt.setString(2, emailId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				result = true;
			}
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closeStatement(pstmt);
		}

		return result;
	}

}
