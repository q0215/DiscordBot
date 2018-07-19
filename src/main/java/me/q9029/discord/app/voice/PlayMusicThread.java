package me.q9029.discord.app.voice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.SearchMatch;

import me.q9029.discord.app.BundleConst;
import me.q9029.discord.app.ResourceBundleUtil;
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
			String dropboxToken = ResourceBundleUtil.getString(BundleConst.DROPBOX_TOKEN);
			DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("java/1.0.0").withUserLocale("ja_JP").build();
			DbxClientV2 dropBoxClient = new DbxClientV2(requestConfig, dropboxToken);

			Long channelId = Long.parseLong(ResourceBundleUtil.getString(BundleConst.CHANNEL_ID));
			IVoiceChannel voiceChannnel = client.getVoiceChannelByID(channelId);
			AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(voiceChannnel.getGuild());

			while (true) {

				logger.info("Search mp3.");
				List<SearchMatch> matchList = dropBoxClient.files().search("", "*.mp3").getMatches();
				List<String> playList = new ArrayList<>();
				for (SearchMatch data : matchList) {
					playList.add(data.getMetadata().getPathDisplay());
				}
				matchList.clear();
				matchList = null;

				Collections.shuffle(playList);

				for (String path : playList) {

					try {
						logger.info("Start " + path);
						logger.info("Join voice channel.");
						voiceChannnel.join();

						logger.info("Get mp3 from dropbox.");
						byte[] byteArray = null;
						try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
							dropBoxClient.files().downloadBuilder(path).download(os);
							os.flush();
							byteArray = os.toByteArray();
						}

						logger.info("Play mp3 in discord.");
						player.clean();

						try (AudioInputStream stream = AudioSystem
								.getAudioInputStream(new ByteArrayInputStream(byteArray))) {

							player.queue(stream);

							logger.info("Wait for the end of stream.");
							while (player.getPlaylistSize() > 0) {
								logger.debug("player.getPlaylistSize() > 0");
								Thread.sleep(1000);
							}
						}
						logger.info("End " + path);

					} catch (Exception e) {
						logger.error("Failed to play " + path, e);
					}
				}
			}
		} catch (Exception e) {
			logger.error("An unexpected exception occurred.", e);
		}
	}
}
