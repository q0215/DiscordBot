package me.q9029.discord.app.listener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.common.DiscordPropsUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class AutoResponseLinstener {

	private static Logger logger = LoggerFactory.getLogger(AutoResponseLinstener.class);

	private static long channelId = Long.parseLong(DiscordPropsUtil.getString(DiscordPropsUtil.Key.CHANNEL_ID));

	private static Map<String, String> autoRespMap = new HashMap<>();

	static {
		String autoRespCsv = DiscordPropsUtil.getString(DiscordPropsUtil.Key.CLASSPATH_RESP_FILE);
		InputStream is = AutoResponseLinstener.class.getResourceAsStream(autoRespCsv);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

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

		// チャンネル制限
		if (channelId == event.getChannel().getLongID()) {

			long startNanoTime = System.nanoTime();

			Map<Integer, String> respMap = new HashMap<>();
			String content = event.getMessage().getContent();

			// 対象文字について
			for (String key : autoRespMap.keySet()) {

				// 文字列出現インデックスの探索
				for (int i = 0; i < content.length(); i++) {

					int index = content.indexOf(key, i);
					if (i == index) {
						respMap.put(i, autoRespMap.get(key));

					} else if (i < index) {
						i = index - 1;

					} else {
						break;
					}
				}
			}

			// 対象文字１件以上
			if (respMap.size() > 0) {

				// 対象文字のインデックスでソート
				List<Integer> indexList = new ArrayList<>(respMap.keySet());
				Collections.sort(indexList);

				// 出現順で文字列組み立て
				StringBuilder sb = new StringBuilder();
				for (Integer index : indexList) {
					sb.append(respMap.get(index));
				}

				// メッセージ送信
				event.getClient().getChannelByID(channelId).sendMessage(sb.toString());
			}

			logger.debug(System.nanoTime() - startNanoTime + "ns elapsed");
		}
	}
}
