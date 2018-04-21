package me.q9029.discord.bot;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

public class DiscordBotMain {

	private static Logger logger = LoggerFactory.getLogger(DiscordBotMain.class);

	public static void main(String[] args) {

		long startMillis = System.nanoTime();
		try {
			logger.info("Start.");

			// client create
			IDiscordClient cli = DiscordClientUtil.getBuiltClient();

			// client login.
			cli.login();

			// send message.
			List<IChannel> chList = cli.getChannels();
			for (IChannel ch : chList) {
				if ("general".equals(ch.getName())) {
					ch.sendMessage("【定期】装備ロック10分前");
				}
			}

			// client logout.
			cli.logout();
			System.exit(0);

		} catch (Exception e) {
			logger.error("An unexpected exception occurred.", e);
			System.exit(1);

		} finally {
			long endMillis = System.nanoTime();
			logger.info(endMillis - startMillis + " ns elapsed.");
			logger.info("End.");
		}
	}
}
