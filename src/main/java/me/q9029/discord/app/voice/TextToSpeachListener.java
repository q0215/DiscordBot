package me.q9029.discord.app.voice;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.BundleConst;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class TextToSpeachListener {

	private static final Logger logger = LoggerFactory.getLogger(TextToSpeachListener.class);

	private static ResourceBundle bundle = ResourceBundle.getBundle(BundleConst.BASE_NAME);
	private static final long textChannelId = Long.parseLong(bundle.getString(BundleConst.CHANNEL_ID));

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
