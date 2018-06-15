package me.q9029.discord.app.voice;

import java.io.File;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.ClientUtil;
import sx.blah.discord.api.IDiscordClient;

public class TextToSpeachMain {

	private static Logger logger = LoggerFactory.getLogger(TextToSpeachMain.class);

	private static IDiscordClient client = null;

	public static void main(String[] args) {

		int exitCode = 0;
		try {
			logger.info("Start.");

			// get token
			ResourceBundle bundle = ResourceBundle.getBundle("text-to-speach");
			String token = bundle.getString("discord.token");

			try {
				// create built client
				client = ClientUtil.getBuiltClient(token);

				// add listener
				TextToSpeachListener listener = new TextToSpeachListener();
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

				TextToSpeachThread.getInstance().start();

				File procFile = new File(bundle.getString("sub.proc.file.path"));
				if (procFile.createNewFile()) {
					while (procFile.exists()) {
						Thread.sleep(1000);
					}
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
		}

		logger.info("End.");
		System.exit(exitCode);
	}
}
