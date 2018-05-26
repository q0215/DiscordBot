package me.q9029.discord;

import java.util.ResourceBundle;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class SouvenirLinstener {

	private static ResourceBundle bundle = ResourceBundle.getBundle("souvenir");
	private static long channelId = Long.parseLong(bundle.getString("channel.id"));

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event)
			throws RateLimitException, DiscordException, MissingPermissionsException {

		if (channelId == event.getChannel().getLongID()) {

			String content = event.getMessage().getContent();

			if (content != null && (content.contains("みやび") || content.contains("ミヤビ") || content.contains("miyabi")
					|| content.contains("ママ") || content.contains("想像") || content.contains("妊娠"))) {
				event.getClient().getChannelByID(channelId).sendMessage(":pregnant_woman:");
			}

			if (content != null && (content.contains("なす") || content.contains("ナス") || content.contains("茄子")
					|| content.contains("nasu"))) {
				event.getClient().getChannelByID(channelId).sendMessage(":eggplant:");
			}

			if (content != null && content.contains("ぴち")) {
				event.getClient().getChannelByID(channelId).sendMessage(":blowfish:");
			}

			if (content != null && content.contains("ぷんすか")) {
				event.getClient().getChannelByID(channelId).sendMessage(":rage:");
			}
		}
	}
}
