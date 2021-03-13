package com.ticketBooking.listeners;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.ticketBooking.common.CONSTANTS;
import com.ticketBooking.config.handler.ConfigHandler;

/**
 * Application Lifecycle Listener implementation class
 * ApplicationContextListener
 * 
 */

public class ApplicationContextListener implements ServletContextListener {
	private static Logger log = Logger.getLogger(ApplicationContextListener.class);

	/**
	 * Default constructor.
	 */
	public ApplicationContextListener() {
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */

	public void contextInitialized(ServletContextEvent event) {

		ServletContext ctx = event.getServletContext();

		String classFolder = ctx.getRealPath("/") + System.getProperty("file.separator") + "WEB-INF"
				+ System.getProperty("file.separator") + "classes" + System.getProperty("file.separator");

		Properties JSLogProperties = new Properties();

		try {

			JSLogProperties.load(new FileInputStream(classFolder + "jslog.properties"));

			log = Logger.getLogger(ApplicationContextListener.class);

		} catch (Exception e) {
			System.out.println("Cannot load jslog.properties");
			System.out.println(e.getMessage());
		}

		Properties configProperties = new Properties();
		String configFile = classFolder + "config.properties";

		try {
			configProperties.load(new FileInputStream(configFile));
			ConfigHandler config = new ConfigHandler();
			config.loadConfigHandler(configProperties);

			CONSTANTS.CONNECTION_URL = configProperties.getProperty("db");
			CONSTANTS.encryptionEnabled = Boolean.valueOf(configProperties.getProperty("encryptionEnabled"));

			log.debug("Initializing Database : " + CONSTANTS.CONNECTION_URL);

		} catch (IOException e) {
			log.debug(e);
		}
		try {

		} catch (Exception e) {
			log.error("", e);
		}

	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */

	public void contextDestroyed(ServletContextEvent arg0) {
	}

}
