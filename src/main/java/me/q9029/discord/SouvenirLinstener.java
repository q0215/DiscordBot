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

			if (content != null && (content.contains("なす") || content.contains("ナス") || content.contains("茄子"))) {
				event.getClient().getChannelByID(channelId).sendMessage(":eggplant:");
			}
		}
	}
}
