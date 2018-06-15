package me.q9029.discord.app;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

public class ClientUtil {

	/**
	 * Get new built discord client.
	 * 
	 * @param token
	 *            discord token
	 * @return client
	 */
	public static IDiscordClient getBuiltClient(String token) {
		return new ClientBuilder().withToken(token).build();
	}
}
