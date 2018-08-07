package me.q9029.discord.app.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.common.DiscordProps;
import me.q9029.discord.app.common.Interruptible;
import me.q9029.discord.app.thread.TextToSpeachThread;
import me.q9029.discord.app.util.DiscordPropsUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class TextToSpeachListener implements Interruptible {

	private static final Logger logger = LoggerFactory.getLogger(TextToSpeachListener.class);

	private static final long textChannelId = Long
			.parseLong(DiscordPropsUtil.getString(DiscordProps.TextToSpeach.CHANNEL_ID));

	private static TextToSpeachThread thread = TextToSpeachThread.getInstance();

	public TextToSpeachListener() {
		thread.start();
	}

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

			thread.addQueue(event);

		} catch (Exception e) {
			logger.error("An unexpected exception occurred.", e);
		}
	}

	@Override
	public void interrupt() {
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
			logger.error("", e);
		}
	}
}
