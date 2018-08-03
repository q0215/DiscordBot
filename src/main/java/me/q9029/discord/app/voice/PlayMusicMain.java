package me.q9029.discord.app.voice;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.common.BundleConst;
import me.q9029.discord.app.common.ClientUtil;
import me.q9029.discord.app.common.DiscordPropsUtil;
import sx.blah.discord.api.IDiscordClient;

public class PlayMusicMain {

	private static Logger logger = LoggerFactory.getLogger(PlayMusicMain.class);

	public static void main(String[] args) {

		int exitCode = 0;
		try {
			logger.info("Start.");

			// create process file
			File procFile = new File(DiscordPropsUtil.getString(BundleConst.PATH_PROC_FILE));
			if (!procFile.createNewFile()) {
				throw new RuntimeException("Failed to create process file.");
			}

			IDiscordClient client = null;
			try {
				// create built client
				client = ClientUtil.getBuiltClient(DiscordPropsUtil.getString(BundleConst.TOKEN));

				// client login
				client.login();

				// add listener
				UploadMp3Listener listener = new UploadMp3Listener();
				client.getDispatcher().registerListener(listener);

				// start thread
				PlayMusicThread thread = new PlayMusicThread(client);
				thread.start();

				// thread sleep
				while (procFile.exists()) {
					Thread.sleep(1000);
				}

			} finally {
				// client logout
				if (client != null) {
					client.logout();
				}
			}
		} catch (Exception e) {
			exitCode = 1;
			logger.error("An unexpected exception occurred.", e);
		}

		logger.info("End.");
		System.exit(exitCode);
	}
}
