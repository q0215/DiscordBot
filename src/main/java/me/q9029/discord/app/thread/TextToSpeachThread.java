package me.q9029.discord.app.thread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.service.TextToSpeachConverter;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.audio.AudioPlayer;

public class TextToSpeachThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(TextToSpeachThread.class);

	private static TextToSpeachThread thread = new TextToSpeachThread();

	private Queue<MessageReceivedEvent> queue = new ConcurrentLinkedQueue<>();

	private TextToSpeachThread() {
	}

	public static TextToSpeachThread getInstance() {
		return thread;
	}

	public void addQueue(MessageReceivedEvent event) {
		queue.add(event);
	}

	@Override
	public void run() {

		// プロセス終了待機処理
		while (true) {

			try {
				// キュー待機処理
				while (queue.size() == 0) {
					logger.debug("queue.size() == 0");
					Thread.sleep(1000);
				}

				// キュー処理
				logger.info("queue.poll()");
				MessageReceivedEvent event = queue.poll();

				IGuild guild = event.getGuild();
				IUser user = event.getAuthor();
				IVoiceChannel voiceChannel = user.getVoiceStateForGuild(guild).getChannel();

				if (voiceChannel == null) {
					logger.info("voiceChannel == null");
					continue;
				}
				if (!voiceChannel.isConnected()) {
					logger.info("voiceChannel.join()");
					voiceChannel.join();
				}

				try (AudioInputStream stream = TextToSpeachConverter.convertToMp3(event.getMessage().getContent())) {

					AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
					player.queue(stream);

					while (player.getPlaylistSize() > 0) {
						logger.debug("player.getPlaylistSize() > 0");
						Thread.sleep(500);
					}
					Thread.sleep(1000);

				}

			} catch (Exception e) {
				logger.error("An unexpected exception occurred.", e);
			}
		}
	}
}
