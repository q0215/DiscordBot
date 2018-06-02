package me.q9029.discord;

import java.io.File;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sx.blah.discord.api.IDiscordClient;

public class SouvenirVoiceBotMain {

	private static Logger logger = LoggerFactory.getLogger(SouvenirVoiceBotMain.class);

	private static IDiscordClient client = null;

	public static void main(String[] args) {

		int exitCode = 0;
		try {
			logger.info("Start.");

			// get token
			ResourceBundle bundle = ResourceBundle.getBundle("discord");
			String token = bundle.getString("bot.token");

			try {
				// create built client
				client = ClientUtil.getBuiltClient(token);

				// add listener
				SouvenirVoiceBotListener listener = new SouvenirVoiceBotListener();
				client.getDispatcher().registerListener(listener);

				// client login
				client.login();

				// wait for establishing connection
				long readyTimeoutMillis = Long.parseLong(bundle.getString("establish.timeout")) * 1000000000;
				long startReadyMillis = System.nanoTime();
				while (!client.isReady()) {
					if (System.nanoTime() - startReadyMillis >= readyTimeoutMillis) {
						throw new RuntimeException("The waiting time for establishing a connection has been exceeded.");
					}
				}

				File procFile = new File(bundle.getString("proc.file.path.souvenir.voice"));
				if (procFile.createNewFile()) {

					while (procFile.exists()) {
						Thread.sleep(1000 * 60);
					}

					procFile.delete();
				}

			} finally {
				// client logout
				if (client != null && client.isLoggedIn()) {
					client.logout();
				}
			}

		} catch (Exception e) {
			exitCode = 1;
			logger.error("An unexpected exception occurred.", e);

		} finally {
			logger.info("End.");
		}

		System.exit(exitCode);
	}
}
