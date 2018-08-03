package me.q9029.discord.app.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.common.DiscordPropsUtil;
import me.q9029.discord.app.thread.TextToSpeachThread;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class TextToSpeachListener {

	private static final Logger logger = LoggerFactory.getLogger(TextToSpeachListener.class);

	private static final long textChannelId = Long
			.parseLong(DiscordPropsUtil.getString(DiscordPropsUtil.Key.CHANNEL_ID));

	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) {

		try {
			// 対象テキストチャンネル以外は対象外
			if (textChannelId != event.getChannel().getLongID()) {
				return;
			}

			// Botは対象外
			if (event.getAuthor().isBot()) {
				return;
			}

			String content = event.getMessage().getContent();
			// 空文字は対象外
			if (content == null || content.isEmpty()) {
				return;
			}
			// 101文字以上は対象外
			if (content.length() > 100) {
				return;
			}
			// URLを含む場合は対象外
			if (content.contains("http://") || content.contains("https://")) {
				return;
			}

			TextToSpeachThread thread = TextToSpeachThread.getInstance();
			thread.addQueue(event);

		} catch (Exception e) {
			logger.error("An unexpected exception occurred.", e);
		}
	}
}
