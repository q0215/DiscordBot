package me.q9029.discord.app.common;

public class DiscordProps {

	public static final String PROC_FILE = "proc_file";
	public static final String TOKEN = "token";
	public static final String THREADS = "threads";
	public static final String LISTENERS = "listeners";

	public class TextToSpeach {
		public static final String CHANNEL_ID = "text_to_speach.channel_id";
		public static final String POLLY_ACCESS_KEY = "text_to_speach.polly.access_key";
		public static final String POLLY_SECRET_KEY = "text_to_speach.polly.secret_key";
		public static final String POLLY_VOCIE_ID_JP = "text_to_speach.polly.voice_id.jp";
		public static final String POLLY_VOCIE_ID_EN = "text_to_speach.polly.voice_id.en";
	}

	public class PlayMusic {
		public static final String CHANNEL_ID = "play_music.channel_id";
		public static final String DROPBOX_TOKEN = "play_music.dropbox.token";
	}

	public class UploadMp3 {
		public static final String CHANNEL_ID = "upload_mp3.channel_id";
		public static final String DROPBOX_TOKEN = "upload_mp3.dropbox.token";
	}

	public class AutoResponse {
		public static final String CHANNEL_ID = "auto_response.channel_id";
		public static final String RESPONSE_CSV = "auto_response.response_csv";
	}

	public class Translation {
		public static final String CHANNEL_ID = "translation.channel_id";
		public static final String TRANSLATOR_KEY = "translation.translator.key";
		public static final String TRANSLATOR_LANG = "translation.translator.lang";
	}
}
