package me.q9029.discord.bot;

import java.util.ResourceBundle;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

public class DiscordClientUtil {

	/**
	 * discord properties.
	 */
	private static ResourceBundle bundle = ResourceBundle.getBundle("discord");

	/**
	 * bot token.
	 */
	private static final String BOT_TOKEN = bundle.getString("bot.token");

	/**
	 * discort client.
	 */
	private static IDiscordClient client = new ClientBuilder().withToken(BOT_TOKEN).build();

	/**
	 * create client with token.
	 * @param BOT_TOKEN
	 * @return
	 */
	public static IDiscordClient getBuiltClient(){
		return client;
	}

}
