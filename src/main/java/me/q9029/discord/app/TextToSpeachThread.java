package me.q9029.discord.app;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.audio.providers.AudioInputStreamProvider;

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

		logger.info("thread start.");

		// プロセス終了待機処理
		while (true) {

			// キュー待機処理
			while (queue.size() == 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// 阻害処理
					logger.error("キュー待機処理で例外が発生しました。", e);
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
			if (voiceChannel.getConnectedUsers().size() < 2) {
				continue;
			}

			try {
				voiceChannel.join();

				IAudioManager audioManager = event.getGuild().getAudioManager();
				AudioInputStream input = converter.convertToMp3(event.getMessage().getContent());
				AudioInputStreamProvider provider = new AudioInputStreamProvider(input);
				audioManager.setAudioProvider(provider);

				// 音声再生待機処理
				while (input.available() < 1024) {
					Thread.sleep(500);
				}
				Thread.sleep(1500);

			} catch (IOException | InterruptedException e) {
				logger.error("音声再生で例外が発生しました。", e);

			} finally {
				voiceChannel.leave();
			}
		}
	}
}
