package me.q9029.discord.app.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sx.blah.discord.api.IDiscordClient;

public class DiscordClientUtil {

	private static Logger logger = LoggerFactory.getLogger(DiscordClientUtil.class);

	private static IDiscordClient client;

	private DiscordClientUtil() {
	}

	public static IDiscordClient getInstance() {

		logger.info("Get current Instance.");

		if (client == null) {
			client = DiscordClientFactory.newInstance();
		}

		return client;
	}
}
