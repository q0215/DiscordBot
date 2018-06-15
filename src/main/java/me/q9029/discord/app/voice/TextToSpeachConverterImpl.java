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

public class TextToSpeachConverterImpl implements TextToSpeachConverter {

	private static final Logger logger = LoggerFactory.getLogger(TextToSpeachConverterImpl.class);

	private AmazonPolly pollyClient;

	public TextToSpeachConverterImpl() {

		ResourceBundle bundle = ResourceBundle.getBundle("text-to-speach");
		String accessKey = bundle.getString("polly.access_key");
		String secretKey = bundle.getString("polly.secret_key");

		// AmazonPollyクライアント作成
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
		AmazonPollyClientBuilder clientBuilder = AmazonPollyClientBuilder.standard();
		clientBuilder.withCredentials(provider).withRegion(Regions.AP_NORTHEAST_1);
		this.pollyClient = clientBuilder.build();
	}

	@Override
	public AudioInputStream convertToMp3(String text) {

		try {
			logger.info("Start converting " + text);

			SynthesizeSpeechRequest synthReq = new SynthesizeSpeechRequest();
			synthReq.withText(text).withVoiceId("Mizuki").withOutputFormat(OutputFormat.Mp3);

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
