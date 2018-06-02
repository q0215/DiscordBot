package me.q9029.discord;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.polly.model.OutputFormat;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.audio.providers.AudioInputStreamProvider;

public class SouvenirVoiceBotListener {

	private static Logger logger = LoggerFactory.getLogger(SouvenirVoiceBotListener.class);

	private final Synthesizer polly = new Synthesizer();

	private static ResourceBundle bundle = ResourceBundle.getBundle("souvenir_voice");

	private static final long GUILD_ID = Long.parseLong(bundle.getString("guild_id"));
	private static final long TEXT_CHANNEL_ID = Long.parseLong(bundle.getString("text_channnel_id"));
	private static final long VOICE_CHANNEL_ID = Long.parseLong(bundle.getString("voice_channnel_id"));

	private static Object lock = new Object();

	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		IDiscordClient client = event.getClient();
		IGuild guild = client.getGuildByID(GUILD_ID);
		IVoiceChannel voiceChannel = guild.getVoiceChannelByID(VOICE_CHANNEL_ID);
		voiceChannel.join();
	}

	@EventSubscriber
	public void onMessageReceivedEvent(final MessageReceivedEvent event) {

		try {
			IMessage message = event.getMessage();
			IGuild guild = message.getGuild();
			if (event.getChannel().getLongID() != TEXT_CHANNEL_ID) {
				return;
			}

			IVoiceChannel voiceChannel = guild.getVoiceChannelByID(VOICE_CHANNEL_ID);
			if (voiceChannel.getConnectedUsers() == null || voiceChannel.getConnectedUsers().size() < 2) {
				return;
			}

			String content = message.getContent();
			String speechContent = content.replaceAll("<.+>", "");
			if (speechContent.isEmpty()) {
				return;
			}

			synchronized (lock) {
				IAudioManager audioManager = guild.getAudioManager();
				AudioInputStream input = polly.synthesize(speechContent, OutputFormat.Mp3);
				AudioInputStreamProvider provider = new AudioInputStreamProvider(input);
				audioManager.setAudioProvider(provider);
			}

		} catch (IOException | UnsupportedAudioFileException e) {
			logger.error("MessageReceivedEvent処理中に例外発生", e);
		}
	}
}
