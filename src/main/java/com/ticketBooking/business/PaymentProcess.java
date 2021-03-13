package com.ticketBooking.business;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ticketBooking.common.DBConnection;
import com.ticketBooking.common.OpenSessions;

public class PaymentProcess {

	public String payment(JSONObject requestObject, JSONObject dataObject) throws SQLException {

		String currentSessionId = requestObject.getString("sessionId");

		String future = null;
		try {
			ExecutorService executor = Executors.newFixedThreadPool(4);
			try {
				List<Future<String>> future1 = executor.invokeAll(Arrays.asList(new MyTask(requestObject)), 120,
						TimeUnit.SECONDS);

				for (Future<String> f : future1) {
					if (!f.isCancelled()) {
						future = f.get();

					} else {
						future = "payment error";
						// OpenSessions.getInstance().removeFromOpenClientSessions(currentSessionId);
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				future = "payemt timedout";
				e.printStackTrace();
				// OpenSessions.getInstance().removeFromOpenClientSessions(currentSessionId);
			} finally {
				executor.shutdown();
			}

			if (future.equalsIgnoreCase("success")) {

				HttpSession userDetailsSession = OpenSessions.getInstance().getOpenClientSessionById(currentSessionId);

				int seatCount = (Integer) userDetailsSession.getAttribute("seatCount");
				String movieId = (String) userDetailsSession.getAttribute("movieId");
				String userId = (String) userDetailsSession.getAttribute("userId");
				String hallId = (String) userDetailsSession.getAttribute("hallId");
				String showId = (String) userDetailsSession.getAttribute("showId");
				JSONArray seatNumbers = (JSONArray) userDetailsSession.getAttribute("seatNumbers");
				boolean isTicketBooked = false;

				Connection conn = null;

				conn = DBConnection.getInstance().getCon();

				isTicketBooked = bookTicketForUser(conn, movieId, seatCount, userId, hallId, showId, seatNumbers);
				if (isTicketBooked) {
					if (updateMoiveDetails(conn, seatCount, movieId, hallId)) {
						updateToOverAllSeats(conn, showId, seatNumbers);
						geticketDetails(conn, movieId, userId, dataObject, showId, seatNumbers);
					}
				}

				// OpenSessions.getInstance().removeFromOpenClientSessions(currentSessionId);

			} else {
				// OpenSessions.getInstance().removeFromOpenClientSessions(currentSessionId);
				return future;
			}
		} finally {
			System.out.println("Session closed" + currentSessionId);
			OpenSessions.getInstance().removeFromOpenClientSessions(currentSessionId);
		}
		return null;
	}

	private void updateToOverAllSeats(Connection conn, String showId, JSONArray seatNumbers) throws SQLException {
		String UPDATE_CONSTANTS_QUERY = "update show_seat_details set seat_numbers= ? where show_id =?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(UPDATE_CONSTANTS_QUERY);

			pstmt.setString(1, seatNumbers.toString());
			pstmt.setString(2, showId);

			pstmt.execute();

		} finally {
			DBConnection.closeStatement(pstmt);
		}

	}

	class MyTask implements Callable<String> {
		JSONObject paymentRequestObject = new JSONObject();

		public MyTask(JSONObject requestObject) {
			this.paymentRequestObject = requestObject;
		}

		public String call() throws Exception {
			System.out.println("test");
			return payMoney(paymentRequestObject);
		}

	}

	private void geticketDetails(Connection conn, String movieId, String userId, JSONObject dataObject, String showId,
			JSONArray seatNumbers) {

		String SELECT_VARIABLE_QUERY = "select bd.user_id,bd.booked_seats,das.cinemahall,das.ticket_rate,das.cgst,das.sgst,das.movies,das.shows "
				+ "from booking_details as bd JOIN dashboard_details as das on bd.movieid =das.movie_id where bd.user_id =? "
				+ "and bd.movieid =? and bd.show_id=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONObject ticketDetailsObject = new JSONObject();
		try {
			pstmt = conn.prepareStatement(SELECT_VARIABLE_QUERY);
			pstmt.setString(1, userId);
			pstmt.setString(2, movieId);
			pstmt.setString(3, showId);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				int bookedSeats = rs.getInt("booked_seats");
				int price = rs.getInt("ticket_rate");
				int cgst = rs.getInt("cgst");
				int sgst = rs.getInt("sgst");

				ticketDetailsObject.put("cinemaHall", rs.getString("cinemahall"));
				ticketDetailsObject.put("userId", rs.getString("user_id"));
				ticketDetailsObject.put("bookedSeats", bookedSeats);
				ticketDetailsObject.put("movie", rs.getString("movies"));
				ticketDetailsObject.put("show", rs.getString("shows"));
				ticketDetailsObject.put("price", price);
				ticketDetailsObject.put("cgst", cgst);
				ticketDetailsObject.put("sgst", sgst);
				ticketDetailsObject.put("seatNumbers", seatNumbers);
				ticketDetailsObject.put("totalPrice", calculateTicketRate(bookedSeats, price, cgst, sgst));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closeStatement(pstmt);
		}
		dataObject.put("ticketDetails", ticketDetailsObject);
	}

	private double calculateTicketRate(int bookedSeats, int price, int cgst, int sgst) {

		double ticketRate = bookedSeats * price;
		double d = cgst;
		double gst = ticketRate * (d / 100);
		double totalPrice = ticketRate + (gst * 2);

		return totalPrice;

	}

	private boolean updateMoiveDetails(Connection conn, int seatCount, String movieId, String hallId)
			throws SQLException {

		String UPDATE_CONSTANTS_QUERY = "update dashboard_details, (select available_seats,booked_seats from dashboard_details where movie_id=?) as dd set dashboard_details.available_seats = dd.available_seats - ?,dashboard_details.booked_seats= dd.booked_seats + ? where movie_id =? and hall_id =?;";
		PreparedStatement pstmt = null;
		boolean result = false;
		try {
			pstmt = conn.prepareStatement(UPDATE_CONSTANTS_QUERY);

			pstmt.setString(1, movieId);
			pstmt.setInt(2, seatCount);
			pstmt.setInt(3, seatCount);
			pstmt.setString(4, movieId);
			pstmt.setString(5, hallId);

			pstmt.execute();

			result = true;

		} finally {
			DBConnection.closeStatement(pstmt);
		}
		return result;

	}

	private boolean bookTicketForUser(Connection conn, String movieId, int seatCount, String userId, String hallId,
			String showId, JSONArray seatNumbers) throws JSONException, SQLException {

		String query = "INSERT INTO booking_details (user_id,movieid,booked_seats,hall_id,seat_numbers,show_id,show_time) values (?,?,?,?,?,?,(select show_time from show_seat_details where show_id =?))";
		PreparedStatement pstmt = null;
		boolean result = false;
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, userId);
			pstmt.setString(2, movieId);
			pstmt.setInt(3, seatCount);
			pstmt.setString(4, hallId);
			pstmt.setString(5, seatNumbers.toString());
			pstmt.setString(6, showId);
			pstmt.setString(7, showId);

			pstmt.execute();
			result = true;
		} finally {
			DBConnection.closeStatement(pstmt);
		}
		return result;
	}

	private String payMoney(JSONObject requestObject) {

		String url = requestObject.optString("url");
		String params = requestObject.optString("params");

		if (params != null && !params.isEmpty()) {
			try {
				if (url.contains("https")) {

					TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return null;
						}

						public void checkClientTrusted(X509Certificate[] certs, String authType) {
						}

						public void checkServerTrusted(X509Certificate[] certs, String authType) {
						}
					} };

					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, trustAllCerts, new java.security.SecureRandom());
					HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

					HostnameVerifier allHostsValid = new HostnameVerifier() {

						public boolean verify(String hostname, SSLSession session) {
							return true;
						}
					};
					HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
				}

				JSONObject resultObject = new JSONObject();
				URL obj = new URL(url);

				HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
				con.setRequestMethod("POST");

				con.setDoOutput(true);
				OutputStream os = con.getOutputStream();

				os.write(params.getBytes());
				os.flush();
				os.close();

				int responseCode = con.getResponseCode();
				resultObject.put("statusCode", String.valueOf(responseCode));

				StringBuffer response = new StringBuffer();
				if (responseCode >= 200 && responseCode < 300) {
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;

					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					String responseStr = "";
					responseStr = response.toString();

					resultObject.put("responseBody", new JSONObject(responseStr));

				} else {
					resultObject.put("responseBody", response.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "error";
			}
		}
		return "success";
	}
}
