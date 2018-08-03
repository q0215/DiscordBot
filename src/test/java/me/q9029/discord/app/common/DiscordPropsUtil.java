package me.q9029.discord.app.common;

import java.util.ResourceBundle;

public class DiscordPropsUtil {

	private static final ResourceBundle bundle = ResourceBundle.getBundle("discord");

	public class Key {
		public static final String TOKEN = "token";
		public static final String TIMEOUT_SEC = "timeout_sec";
		public static final String THREADS = "threads";
		public static final String LISTENERS = "listeners";
		public static final String CHANNEL_ID = "channel_id";
		public static final String VOICE_CHANNEL_ID = "voice_channel_id";
		public static final String UPLOAD_CHANNEL_ID = "upload_channel_id";
		public static final String PATH_PROC_FILE = "path.proc_file";
		public static final String CLASSPATH_RESP_FILE = "classpath.resp_file";
		public static final String POLLY_ACCESS_KEY = "polly.access_key";
		public static final String POLLY_SECRET_KEY = "polly.secret_key";
		public static final String POLLY_VOCIE_ID = "polly.voice_id";
		public static final String POLLY_VOCIE_ID_EN = "polly.voice_id.en_US";
		public static final String DROPBOX_TOKEN = "dropbox.token";
		public static final String MICROSOFTTRANSLATOR_KEY = "microsofttranslator.key";
		public static final String MICROSOFTTRANSLATOR_LANG = "microsofttranslator.lang";
	}

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
