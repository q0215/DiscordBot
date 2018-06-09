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

		// 接続中ボイスチャット
		IVoiceChannel voiceChannel = null;

		// プロセス終了待機処理
		while (true) {

			try {
				// キュー待機処理
				int count = 0;
				while (queue.size() == 0) {
					logger.debug("queue.size() == 0");
					TextToSpeachThread.sleep(1000);

					if (voiceChannel != null && count++ >= 20 && voiceChannel.isConnected()) {
						logger.debug("voiceChannel.leave()");
						voiceChannel.getGuild().getConnectedVoiceChannel().leave();
						voiceChannel = null;
					}
				}

				// キュー処理
				MessageReceivedEvent event = queue.poll();

				IGuild guild = event.getGuild();
				IUser user = event.getAuthor();
				voiceChannel = user.getVoiceStateForGuild(guild).getChannel();

				if (voiceChannel == null) {
					continue;
				}
				if (!voiceChannel.isConnected()) {
					voiceChannel.join();
				}

				AudioInputStream stream = converter.convertToMp3(event.getMessage().getContent());
				try {
					AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
					player.queue(stream);

					while (player.getPlaylistSize() > 0) {
						logger.debug("player.getPlaylistSize() > 0");
						Thread.sleep(500);
					}
					Thread.sleep(1000);

				} finally {
					if (stream != null) {
						logger.debug("stream.close()");
						stream.close();
					}
				}

			} catch (Exception e) {
				// 例外を握り潰して処理継続
				logger.error("予期せぬエラーが発生しました。", e);
			}
		}
	}
}
