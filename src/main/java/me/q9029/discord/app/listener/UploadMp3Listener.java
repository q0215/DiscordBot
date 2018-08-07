package me.q9029.discord.app.listener;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.UploadUploader;

import me.q9029.discord.app.common.DiscordProps;
import me.q9029.discord.app.common.NoX509TrustManager;
import me.q9029.discord.app.util.DiscordPropsUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IMessage.Attachment;

public class UploadMp3Listener {

	private static final Logger logger = LoggerFactory.getLogger(UploadMp3Listener.class);

	private static final String channelId = DiscordPropsUtil.getString(DiscordProps.UploadMp3.CHANNEL_ID);

	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) {

		// 対象チャンネル以外は除外
		if (!channelId.equals(event.getChannel().getStringID())) {
			return;
		}

		// botは対象外
		if (event.getAuthor().isBot()) {
			return;
		}

		// メッセージから添付ファイルのリスト取得
		IMessage message = event.getMessage();
		List<Attachment> attachmentList = message.getAttachments();

		// 添付ファイルがない場合は対象外
		if (attachmentList == null || attachmentList.size() == 0) {
			return;
		}

		logger.info("Start.");
		for (Attachment attachment : attachmentList) {

			// ファイル名の取得
			String fileName = attachment.getFilename();
			logger.info(fileName);

			// mp3ファイル以外は対象外
			if (!fileName.contains("mp3")) {
				continue;
			}

			// 添付ファイルのURL取得
			String url = attachment.getUrl();
			logger.info(url);

			try {
				// HTTPS通信の開始
				HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
				conn.setRequestProperty("User-agent", "Mozilla/5.0");
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(null, new NoX509TrustManager[] { new NoX509TrustManager() }, null);
				SSLSocketFactory factory = ctx.getSocketFactory();
				conn.setSSLSocketFactory(factory);

				String dropboxToken = DiscordPropsUtil.getString(DiscordProps.UploadMp3.DROPBOX_TOKEN);
				DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("java/1.0.0").withUserLocale("ja_JP")
						.build();
				DbxClientV2 dropBoxClient = new DbxClientV2(requestConfig, dropboxToken);

				try (UploadUploader uploader = dropBoxClient.files().upload("/" + System.nanoTime() + ".mp3")) {
					try (InputStream in = conn.getInputStream()) {
						uploader.uploadAndFinish(in);
						logger.info("Complete to upload " + url);
						event.getChannel().sendMessage(url + "のアップロードが完了しました。");
					}
				}

			} catch (Exception e) {
				logger.error("Failed to upload " + url, e);
				event.getChannel().sendMessage(url + "のアップロードに失敗しました。");
			}
		}
		logger.info("End.");
	}
}
