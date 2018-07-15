package me.q9029.discord.app.voice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.SearchMatch;

import me.q9029.discord.app.BundleConst;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.audio.AudioPlayer;

public class PlayMusicThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(PlayMusicThread.class);

	private IDiscordClient client;

	public PlayMusicThread(IDiscordClient client) {
		this.client = client;
	}

	@Override
	public void run() {

		try {
			ResourceBundle bundle = ResourceBundle.getBundle(BundleConst.BASE_NAME);
			String dropboxToken = bundle.getString(BundleConst.DROPBOX_TOKEN);
			DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("java/1.0.0").withUserLocale("ja_JP").build();

			Long channelId = Long.parseLong(bundle.getString(BundleConst.CHANNEL_ID));
			IVoiceChannel voiceChannnel = client.getVoiceChannelByID(channelId);

			while (true) {

				logger.info("Search mp3.");
				DbxClientV2 dropBoxClient = new DbxClientV2(requestConfig, dropboxToken);
				List<SearchMatch> matchList = dropBoxClient.files().search("", "*.mp3").getMatches();
				Collections.shuffle(matchList);

				for (SearchMatch data : matchList) {

					try {
						logger.info("Start " + data.getMetadata().getPathDisplay());
						logger.info("Join voice channel.");
						voiceChannnel.join();

						logger.info("Get mp3 from dropbox.");
						byte[] byteArray = null;
						try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
							dropBoxClient.files().downloadBuilder(data.getMetadata().getPathDisplay()).download(os);
							os.flush();
							byteArray = os.toByteArray();
						}

						logger.info("Play mp3 in discord.");
						try (InputStream is = new ByteArrayInputStream(byteArray);
								AudioInputStream stream = AudioSystem.getAudioInputStream(is)) {

							AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(voiceChannnel.getGuild());
							player.queue(stream);

							logger.info("Wait for the end of stream.");
							while (player.getPlaylistSize() > 0) {
								logger.debug("player.getPlaylistSize() > 0");
								Thread.sleep(500);
							}
						}
						logger.info("End " + data.getMetadata().getPathDisplay());

					} catch (Exception e) {
						logger.error("Failed to play " + data.getMetadata().getPathDisplay(), e);
					}
				}
			}
		} catch (Exception e) {
			logger.error("An unexpected exception occurred.", e);
		}
	}
}
