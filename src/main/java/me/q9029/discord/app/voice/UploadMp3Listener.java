package me.q9029.discord.app.voice;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.ResourceBundle;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.UploadUploader;

import me.q9029.discord.app.BundleConst;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IMessage.Attachment;

public class UploadMp3Listener {

	private static final Logger logger = LoggerFactory.getLogger(UploadMp3Listener.class);

	private static ResourceBundle bundle = ResourceBundle.getBundle(BundleConst.BASE_NAME);
	private static final String channelId = bundle.getString(BundleConst.UPLOAD_CHANNEL_ID);

	@EventSubscriber
	public synchronized void onMessageReceivedEvent(MessageReceivedEvent event) {

		logger.debug("Start.");
		if (!channelId.equals(event.getChannel().getStringID())) {
			return;
		}

		IMessage message = event.getMessage();
		List<Attachment> attachmentList = message.getAttachments();

		for (Attachment attachment : attachmentList) {

			logger.debug(attachment.getFilename());
			if (attachment.getFilename().contains("mp3")) {

				try {
					logger.debug(attachment.getUrl());
					SSLContext ctx = SSLContext.getInstance("TLS");
					ctx.init(null, new NonAuthentication[] { new NonAuthentication() }, null);
					SSLSocketFactory factory = ctx.getSocketFactory();

					URL url = new URL(attachment.getUrl());
					HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
					conn.setSSLSocketFactory(factory);
					conn.setRequestProperty("User-agent", "Mozilla/5.0");

					try (InputStream in = conn.getInputStream()) {

						ResourceBundle bundle = ResourceBundle.getBundle(BundleConst.BASE_NAME);
						String dropboxToken = bundle.getString(BundleConst.DROPBOX_TOKEN);
						DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("java/1.0.0")
								.withUserLocale("ja_JP").build();
						DbxClientV2 dropBoxClient = new DbxClientV2(requestConfig, dropboxToken);

						UploadUploader uploader = dropBoxClient.files().upload("/" + System.nanoTime() + ".mp3");
						uploader.uploadAndFinish(in);

						event.getChannel().sendMessage(attachment.getFilename() + "のアップロードが完了しました。");
					}

				} catch (IOException | DbxException | NoSuchAlgorithmException | KeyManagementException e) {
					logger.error("", e);
				}
			}
		}
		logger.debug("End.");
	}

	class NonAuthentication implements X509TrustManager {
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
