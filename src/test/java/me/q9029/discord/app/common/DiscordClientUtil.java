package me.q9029.discord.app.common;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

public class DiscordClientUtil {

	private static IDiscordClient client = new ClientBuilder()
			.withToken(DiscordPropsUtil.getString(DiscordPropsUtil.Key.TOKEN)).build();

	public static IDiscordClient getClient() {
		return client;
	}
}
