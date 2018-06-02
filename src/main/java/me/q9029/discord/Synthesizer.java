package me.q9029.discord;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.Voice;

public class Synthesizer {

	private final AmazonPolly pollyClient;
	private final String languageCode = "ja-JP";
	private final Voice voice;
	private final Regions regions = Regions.AP_NORTHEAST_1;
	private static ResourceBundle bundle = ResourceBundle.getBundle("souvenir_voice");
	private final static String ACCESS_KEY = bundle.getString("polly.access_key");
	private final static String SECRET_KEY = bundle.getString("polly.secret_key");

	public Synthesizer() {
		BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
		pollyClient = AmazonPollyClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(regions).build();
		DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest().withLanguageCode(languageCode);
		DescribeVoicesResult describeVoicesResult = pollyClient.describeVoices(describeVoicesRequest);
		List<Voice> voices = describeVoicesResult.getVoices();
		this.voice = voices.get(1);
	}

	public AudioInputStream synthesize(final String text, final OutputFormat format)
			throws UnsupportedAudioFileException, IOException {

		SynthesizeSpeechRequest synthReq = new SynthesizeSpeechRequest().withText(text).withVoiceId(voice.getId())
				.withOutputFormat(format);
		SynthesizeSpeechResult synthRes = pollyClient.synthesizeSpeech(synthReq);

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		while (true) {
			int len = synthRes.getAudioStream().read(buffer);
			if (len < 0) {
				break;
			}
			bout.write(buffer, 0, len);
		}
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(bout.toByteArray()));
		return audioStream;
	}
}
