package me.q9029.discord.app;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.audio.AudioPlayer;

public class TextToSpeachThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(TextToSpeachThread.class);

	private static Queue<MessageReceivedEvent> queue = new ConcurrentLinkedQueue<>();

	private static TextToSpeachThread thread = new TextToSpeachThread();
	private TextToSpeachConverter converter = new TextToSpeachConverterImpl();

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

		logger.debug("スレッド処理開始");

		// プロセス終了待機処理
		while (true) {

			// キュー待機処理
			while (queue.size() == 0) {
				try {
					logger.debug("キュー待機");
					TextToSpeachThread.sleep(1000);
				} catch (InterruptedException e) {
					// 阻害処理
					logger.error("キュー待機で例外が発生しました。", e);
				}
			}

			// キュー処理
			MessageReceivedEvent event = queue.poll();

			IGuild guild = event.getGuild();
			IUser user = event.getAuthor();
			IVoiceChannel voiceChannel = user.getVoiceStateForGuild(guild).getChannel();

			if (voiceChannel == null) {
				continue;
			}

			try {
				voiceChannel.join();

				AudioInputStream stream = converter.convertToMp3(event.getMessage().getContent());
				AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);

				player.queue(stream);

				while (player.getPlaylistSize() > 0) {
					logger.info("00000");
					Thread.sleep(500);
				}
				Thread.sleep(500);

				player.clear();
				player.clean();
				stream.close();

			} catch (Exception e) {
				logger.error("再生で例外が発生しました。", e);

			} finally {
				voiceChannel.leave();
			}
		}
	}
}
