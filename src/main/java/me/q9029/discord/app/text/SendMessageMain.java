package me.q9029.discord.app.text;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.common.BundleConst;
import me.q9029.discord.app.common.ClientUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.ICategory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.MissingPermissionsException;

public class SendMessageMain {

	private static Logger logger = LoggerFactory.getLogger(SendMessageMain.class);

	public static void main(String[] args) {

		int exitCode = 0;
		long startMillis = System.nanoTime();
		try {
			logger.info("Start.");

			// check arguments
			if (args == null || args.length != 2) {
				throw new RuntimeException("Missing arguments.");
			}
			long channelId = Long.parseLong(args[0]);
			String message = args[1];

			// get token
			ResourceBundle bundle = ResourceBundle.getBundle(BundleConst.BASE_NAME);
			String token = bundle.getString(BundleConst.TOKEN);

			IDiscordClient client = null;
			try {
				// create built client
				client = ClientUtil.getBuiltClient(token);

				// client login
				client.login();

				// wait for establishing connection
				long timeoutMillis = Long.parseLong(bundle.getString(BundleConst.TIMEOUT_SEC)) * 1000000000;
				long startReadyMillis = System.nanoTime();
				while (!client.isReady()) {
					if (System.nanoTime() - startReadyMillis >= timeoutMillis) {
						throw new RuntimeException("The waiting time for establishing a connection has been exceeded.");
					}
				}

				// send message
				IChannel channel = client.getChannelByID(channelId);
				try {
					channel.sendMessage(message);

				} catch (MissingPermissionsException e) {
					ICategory category = channel.getCategory();
					logger.warn("Missing the permission to send message. Guild_Name:" + channel.getGuild().getName()
							+ " Category_Name:" + (category != null ? category.getName() : "null") + " Channel_Name:"
							+ channel.getName(), e);

				} catch (Exception e) {
					ICategory category = channel.getCategory();
					logger.error("Failed to send message. Guild_Name:" + channel.getGuild().getName()
							+ " Category_Name:" + (category != null ? category.getName() : "null") + " Channel_Name:"
							+ channel.getName(), e);
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

		logger.info(System.nanoTime() - startMillis + " ns elapsed.");
		logger.info("End.");
		System.exit(exitCode);
	}
}
