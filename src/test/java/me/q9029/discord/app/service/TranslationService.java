package me.q9029.discord.app.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;

import me.q9029.discord.app.common.DiscordPropsUtil;

public class TranslationService {

	private static final String key;
	private static final String host;

	static {
		String lang = DiscordPropsUtil.getString(DiscordPropsUtil.Key.MICROSOFTTRANSLATOR_LANG);

		host = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=" + lang;
		key = DiscordPropsUtil.getString(DiscordPropsUtil.Key.MICROSOFTTRANSLATOR_KEY);
	}

	public String translate(String text) throws Exception {

		URL url = new URL(host);

		List<RequestBody> objList = new ArrayList<>();
		objList.add(new RequestBody(text));
		String content = new Gson().toJson(objList);

		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Content-Length", "" + content.length());
		connection.setRequestProperty("Ocp-Apim-Subscription-Key", key);
		connection.setRequestProperty("X-ClientTraceId", java.util.UUID.randomUUID().toString());
		connection.setDoOutput(true);

		try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
			byte[] encoded_content = content.getBytes("UTF-8");
			wr.write(encoded_content, 0, encoded_content.length);
			wr.flush();
			wr.close();
		}

		StringBuilder response = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
			String line;
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();
		}

		return response.toString();
	}

	private class RequestBody {

		private String text;

		private RequestBody(String text) {
			this.setText(text);
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}
}
