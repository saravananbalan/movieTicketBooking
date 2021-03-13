package com.ticketBooking.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

public class DBConnection {

	private static Logger log = Logger.getLogger(DBConnection.class);

	private static DBConnection instance = null;
	private InitialContext context;
	private BasicDataSource datasource;
	private BasicDataSource usr_datasource;

	private DBConnection() {

		try {
			log.debug("DB-JNDI=" + CONSTANTS.CONNECTION_URL);
			context = new InitialContext();
			datasource = (BasicDataSource) context.lookup(CONSTANTS.CONNECTION_URL);
		} catch (NamingException e) {
			log.error("Naming Exception", e);
		} catch (Exception e) {
			log.error("General Exception", e);
		}
	}

	public static DBConnection getInstance() {
		if (instance == null)
			instance = new DBConnection();

		return instance;
	}

	/**
	 * function to create a db connection to
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getCon() throws SQLException {
		Connection c = datasource.getConnection();

		log.debug("Opening IAN source connection : " + System.identityHashCode(c));

		return c;
	}

	/**
	 * function to create a db connection to msfsource
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {

		Connection c = datasource.getConnection();

		log.debug("Opening datasource connection : " + System.identityHashCode(c));

		return c;
	}

	public void releasePool() {

		try {

			if (datasource != null)
				datasource.close();
			log.info("Closing datasource " + datasource);

			if (usr_datasource != null)
				usr_datasource.close();
			log.info("Closing datasource " + usr_datasource);

		} catch (SQLException e) {
			log.warn("Closing datasource ", e);
		}

	}

	/**
	 * General function to closing the Connection connection
	 * 
	 * @param connection
	 */
	public static void closeConnection(Connection connection) {

		if (connection != null) {

			try {

				log.debug("Closing connection " + System.identityHashCode(connection));
				connection.close();

			} catch (SQLException e) {
				log.warn(e);
			}
			connection = null;
		}
	}

	/**
	 * General function to closing the Statement st
	 * 
	 * @param st
	 */
	public static void closeStatement(Statement st) {

		if (st != null) {

			try {

				log.debug("Closing statment " + System.identityHashCode(st));
				st.close();

			} catch (SQLException e) {
				log.warn(e);
			}
			st = null;
		}
	}

	/**
	 * General function to closing the Resultset rs
	 * 
	 * @param rs
	 */
	public static void closeResultSet(ResultSet rs) {

		if (rs != null) {

			try {

				log.debug("Closing resultset " + System.identityHashCode(rs));
				rs.close();
			} catch (SQLException e) {
				log.warn(e);
			}
			rs = null;
		}
	}

	/**
	 * general function to create prepared statment
	 * 
	 * @param query
	 * @param conn
	 * @return
	 */
	public static PreparedStatement createPreparedStatement(String query, Connection conn) {

		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(query);
		} catch (SQLException e) {
			log.error("error while creating ps ---> ", e);
			return null;
		}

		return ps;
	}
}
