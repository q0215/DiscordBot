package me.q9029.discord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class SouvenirLinstener {

	private static Logger logger = LoggerFactory.getLogger(SouvenirLinstener.class);

	private static ResourceBundle bundle = ResourceBundle.getBundle("souvenir");
	private static long channelId = Long.parseLong(bundle.getString("channel.id"));

	private static Map<String, String> autoRespMap = new HashMap<>();

	static {
		File file = new File(bundle.getString("auto.response.csv"));
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			String line;
			while ((line = br.readLine()) != null) {
				String[] record = line.split(",", 0);
				autoRespMap.put(record[0], record[1]);
			}

			for (String key : autoRespMap.keySet()) {
				logger.debug(key + " " + autoRespMap.get(key));
			}

		} catch (Exception e) {
			logger.error("file load error", e);
		}
	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event)
			throws RateLimitException, DiscordException, MissingPermissionsException {

		if (channelId == event.getChannel().getLongID()) {

			long startNanoTime = System.nanoTime();

			Map<Integer, String> respMap = new HashMap<>();
			String content = event.getMessage().getContent();

			for (String key : autoRespMap.keySet()) {

				for (int i = 0; i < content.length(); i++) {

					int index = content.indexOf(key, i);
					if (index != -1 && index == i) {
						respMap.put(index, autoRespMap.get(key));
						logger.info("要素" + i + " " + autoRespMap.get(key));
					}
				}
			}

			List<Integer> indexList = new ArrayList<>(respMap.keySet());
			Collections.sort(indexList);

			StringBuilder sb = new StringBuilder();
			for (Integer index : indexList) {
				logger.info("抽出：" + index);
				sb.append(respMap.get(index));
			}
			event.getClient().getChannelByID(channelId).sendMessage(sb.toString());

			logger.debug(System.nanoTime() - startNanoTime + "ns elapsed");
		}
	}
}
