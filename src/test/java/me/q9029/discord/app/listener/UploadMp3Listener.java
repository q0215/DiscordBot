package me.q9029.discord.app.listener;

import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.UploadUploader;

import me.q9029.discord.app.common.DiscordPropsUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IMessage.Attachment;

public class UploadMp3Listener {

	private static final Logger logger = LoggerFactory.getLogger(UploadMp3Listener.class);

	private static final String channelId = DiscordPropsUtil.getString(DiscordPropsUtil.Key.UPLOAD_CHANNEL_ID);

	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) {

		logger.info("Start.");

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

		for (Attachment attachment : attachmentList) {

			logger.info(attachment.getFilename());

			// mp3ファイル以外は対象外
			if (!attachment.getFilename().contains("mp3")) {
				continue;
			}

			try {
				logger.info(attachment.getUrl());
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(null, new NonAuthentication[] { new NonAuthentication() }, null);
				SSLSocketFactory factory = ctx.getSocketFactory();
				URL url = new URL(attachment.getUrl());
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setSSLSocketFactory(factory);
				conn.setRequestProperty("User-agent", "Mozilla/5.0");

				String dropboxToken = DiscordPropsUtil.getString(DiscordPropsUtil.Key.DROPBOX_TOKEN);
				DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("java/1.0.0").withUserLocale("ja_JP")
						.build();
				DbxClientV2 dropBoxClient = new DbxClientV2(requestConfig, dropboxToken);

				UploadUploader uploader = dropBoxClient.files().upload("/" + System.nanoTime() + ".mp3");

				try (InputStream in = conn.getInputStream()) {

					uploader.uploadAndFinish(in);
					logger.info("Complete to upload " + attachment.getUrl());
					event.getChannel().sendMessage(attachment.getUrl() + "のアップロードが完了しました。");
				}

			} catch (Exception e) {
				logger.error("Failed to upload " + attachment.getUrl(), e);
				event.getChannel().sendMessage(attachment.getUrl() + "のアップロードに失敗しました。");
			}
		}
		logger.info("End.");
	}

	private class NonAuthentication implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
