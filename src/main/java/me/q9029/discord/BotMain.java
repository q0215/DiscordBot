package me.q9029.discord;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.ICategory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.MissingPermissionsException;

public class BotMain {

	private static Logger logger = LoggerFactory.getLogger(BotMain.class);

	public static void main(String[] args) {

		int exitCode = 0;
		long startMillis = System.nanoTime();
		try {
			logger.info("Start.");

			// check args
			if (args == null || args.length != 2) {
				throw new RuntimeException("Missing arguments.");
			}
			long channelId = Long.parseLong(args[0]);
			String message = args[1];

			// get token
			ResourceBundle bundle = ResourceBundle.getBundle("discord");
			String token = bundle.getString("bot.token");

			IDiscordClient client = null;
			try {
				// create built client
				client = ClientUtil.getBuiltClient(token);

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

				// send message
				IChannel channel = client.getChannelByID(channelId);
				try {
					channel.sendMessage(message);

				} catch (MissingPermissionsException e) {
					ICategory category = channel.getCategory();
					logger.warn("Missing the permission to send messages. Guild_Name:" + channel.getGuild().getName()
							+ (category != null ? " Category_Name:" + category.getName() : "") + " Channel_Name:"
							+ channel.getName());

				} catch (Exception e) {
					logger.error("Failed to send messages. Channel_ID:" + channelId);
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
			long endMillis = System.nanoTime();
			logger.info("End.");
			logger.info(endMillis - startMillis + " ns elapsed.");
		}

		System.exit(exitCode);
	}
}
