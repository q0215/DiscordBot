package me.q9029.discord.app.util;

import java.util.ResourceBundle;

public class DiscordPropsUtil {

	private static final ResourceBundle bundle = ResourceBundle.getBundle("discord");

	public static String getString(String key) {
		return bundle.getString(key);
	}

	public static int getInteger(String key) {
		return Integer.parseInt(bundle.getString(key));
	}

	public static long getLong(String key) {
		return Long.parseLong(bundle.getString(key));
	}
}
