package me.q9029.discord.bot;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class DiscordUtils {

	/**
	 * send message.
	 * @param channel
	 * @param message
	 */
	public static void sendMessage(IChannel channel, String message){

		RequestBuffer.request(() -> {
			try{
				channel.sendMessage(message);
			} catch (DiscordException e){
				e.printStackTrace();
			}
		});
	}
}
