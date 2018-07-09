package me.q9029.discord.app.voice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
			String dropboxToken = bundle.getString(BundleConst.DISCORD_TOKEN);
			DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("java/1.0.0").withUserLocale("ja_JP").build();
			DbxClientV2 dropBoxClient = new DbxClientV2(requestConfig, dropboxToken);

			Long channelId = Long.parseLong(bundle.getString(BundleConst.CHANNEL_ID));
			IVoiceChannel voiceChannnel = client.getVoiceChannelByID(channelId);
			AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(voiceChannnel.getGuild());

			while (true) {

				try {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					List<SearchMatch> matchList = dropBoxClient.files().search("", "*.mp3").getMatches();
					for (SearchMatch data : matchList) {

						while (player.getPlaylistSize() > 0) {
							logger.debug("player.getPlaylistSize() > 0");
							Thread.sleep(500);
						}

						logger.debug(data.getMetadata().getPathDisplay());
						voiceChannnel.join();
						dropBoxClient.files().downloadBuilder(data.getMetadata().getPathDisplay()).download(os);
						os.flush();
						byte[] byteArray = os.toByteArray();
						os.close();

						AudioInputStream stream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(byteArray));
						player.queue(stream);
					}

				} catch (Exception e) {
					logger.error("", e);
				}
			}
		} catch (Exception e) {
			logger.error("An unexpected exception occurred.", e);
		}
	}
}
