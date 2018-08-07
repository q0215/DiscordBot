package me.q9029.discord.app.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.common.DiscordProps;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

public class DiscordClientFactory {

	private static Logger logger = LoggerFactory.getLogger(DiscordClientFactory.class);

	private static String token = DiscordPropsUtil.getString(DiscordProps.TOKEN);

	private DiscordClientFactory() {
	}

	public static IDiscordClient newInstance() {
		logger.info("Create new client.");
		return new ClientBuilder().withToken(token).build();
	}
}
