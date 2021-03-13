package com.ticketBooking.config.handler;

import java.util.Properties;

public class ConfigHandler {

	Properties configProperties = new Properties();

	public void loadConfigHandler(Properties configFile) {

		this.configProperties = configFile;

	}

	public Properties getConfigHandler() {
		return configProperties;
	}

}
