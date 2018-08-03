package me.q9029.discord.app.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.common.DiscordPropsUtil;
import me.q9029.discord.app.service.TranslationService;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class TranslationLinstener {

	private static Logger logger = LoggerFactory.getLogger(TranslationLinstener.class);

	private static long channelId = Long.parseLong(DiscordPropsUtil.getString(DiscordPropsUtil.Key.CHANNEL_ID));

	private static TranslationService service = new TranslationService();

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event)
			throws RateLimitException, DiscordException, MissingPermissionsException {

		// チャンネル制限
		if (channelId == event.getChannel().getLongID()) {

			long startNanoTime = System.nanoTime();

			String content = event.getMessage().getContent();

			// 空文字は対象外
			if (content == null || content.isEmpty()) {
				return;
			}

			// URLを含む場合は対象外
			if (content.contains("http://") || content.contains("https://")) {
				return;
			}

			try {
				logger.info(content);

				String json = service.translate(content);

				int startIndex = json.indexOf("\"text\":");
				int endIndex = json.indexOf("\",\"to\"", startIndex);

				// 検索文字が見つからない場合は終了
				if (startIndex < 0 || endIndex < 0) {
					return;
				}

				String message = json.substring(startIndex + 8, endIndex);
				logger.info(message);
				event.getChannel().sendMessage(message);

			} catch (Exception e) {
				logger.error("", e);
			}

			logger.debug(System.nanoTime() - startNanoTime + "ns elapsed");
		}
	}
}
