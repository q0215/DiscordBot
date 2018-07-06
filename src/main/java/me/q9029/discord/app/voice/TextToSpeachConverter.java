package me.q9029.discord.app.voice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;

import me.q9029.discord.app.BundleConst;

public class TextToSpeachConverter {

	private static final Logger logger = LoggerFactory.getLogger(TextToSpeachConverter.class);

	private static AmazonPolly pollyClient;
	private static String voiceIdJapanese;
	private static String voiceIdEnglish;

	private static final String HALF_WIDTH_WITHOUT_KANA = "^[\\uff01-\\uff60]+$";

	static {
		ResourceBundle bundle = ResourceBundle.getBundle(BundleConst.BASE_NAME);
		String accessKey = bundle.getString(BundleConst.POLLY_ACCESS_KEY);
		String secretKey = bundle.getString(BundleConst.POLLY_SECRET_KEY);
		voiceIdJapanese = bundle.getString(BundleConst.POLLY_VOCIE_ID);
		voiceIdEnglish = bundle.getString(BundleConst.POLLY_VOCIE_ID_EN);

		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
		AmazonPollyClientBuilder clientBuilder = AmazonPollyClientBuilder.standard();
		clientBuilder.withCredentials(provider).withRegion(Regions.AP_NORTHEAST_1);
		pollyClient = clientBuilder.build();
	}

	public static AudioInputStream convertToMp3(String text) {
		try {
			logger.info("Start converting " + text);

			String voiceId;
			if (text.length() == text.getBytes().length) {
				voiceId = voiceIdEnglish;
			} else {
				voiceId = voiceIdJapanese;
			}

			SynthesizeSpeechRequest synthReq = new SynthesizeSpeechRequest();
			synthReq.withText(text).withVoiceId(voiceId).withOutputFormat(OutputFormat.Mp3);

			SynthesizeSpeechResult synthRes = pollyClient.synthesizeSpeech(synthReq);

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			while (true) {
				int len = synthRes.getAudioStream().read(buffer);
				if (len < 0) {
					break;
				}
				byteArrayOutputStream.write(buffer, 0, len);
			}

			InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);

			logger.info("End converting " + text);
			return audioInputStream;

		} catch (Exception e) {
			throw new RuntimeException("An Exception occurred while invoking Amazon Polly.", e);
		}
	}
}
