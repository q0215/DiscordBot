package me.q9029.discord;

import java.util.ResourceBundle;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class RoleManageLinstener {

	private static ResourceBundle bundle = ResourceBundle.getBundle("role");
	private static long channelId = Long.parseLong(bundle.getString("channel.id"));

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event)
			throws RateLimitException, DiscordException, MissingPermissionsException {

		if (channelId == event.getChannel().getLongID()) {

			IUser user = event.getAuthor();
			IGuild guild = event.getGuild();
			String content = event.getMessage().getContent();

			for (IRole role : guild.getRoles()) {

				if (!"@everyone".equals(role.getName()) && user.hasRole(role)) {
					user.removeRole(role);
				}
			}

			if (bundle.containsKey("role." + content)) {

				long roleId = Long.parseLong(bundle.getString("role." + content));
				user.addRole(guild.getRoleByID(roleId));
				event.getClient().getChannelByID(channelId).sendMessage(user.getName() + "さんを" + content + "に設定しました。");
			}
		}
	}
}
