package me.q9029.discord.app.voice;

import java.io.File;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class TextToSpeachControllerListener {

	private static final Logger logger = LoggerFactory.getLogger(TextToSpeachControllerListener.class);

	private static ResourceBundle bundle = ResourceBundle.getBundle("text-to-speach");
	private static final long guildId = Long.parseLong(bundle.getString("discord.guild.id"));
	private static final long textChannelId = Long.parseLong(bundle.getString("discord.text.channel.id"));

	private static final String subProcShell = bundle.getString("sub.proc.shell.path");
	private static final File subProcFile = new File(bundle.getString("sub.proc.file.path"));
	private static final String startCommand = bundle.getString("sub.proc.start.cmd");
	private static final String endCommand = bundle.getString("sub.proc.end.cmd");

	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) {

		try {
			// 対象ギルド以外は対象外
			if (guildId != event.getGuild().getLongID()) {
				return;
			}
			// 対象テキストチャンネル以外は対象外
			if (textChannelId != event.getChannel().getLongID()) {
				return;
			}

			// Botは対象外
			if (event.getAuthor().isBot()) {
				return;
			}

			String content = event.getMessage().getContent();
			if (startCommand.equals(content)) {
				if (!subProcFile.exists()) {
					logger.info("Start " + subProcShell);
					new ProcessBuilder("/bin/bash", subProcShell).start();
				}

			} else if (endCommand.equals(content)) {
				if (subProcFile.exists()) {
					logger.info("Delete " + subProcFile);
					subProcFile.delete();
				}
			}

		} catch (Exception e) {
			logger.error("An unexpected exception occurred.", e);
		}
	}
}
