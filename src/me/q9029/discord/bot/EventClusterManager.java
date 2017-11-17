package me.q9029.discord.bot;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class EventClusterManager {

	private static Logger LOGGER = LoggerFactory.getLogger(EventClusterManager.class);

	private static final String BOT_PREFIX = "<@380778136431755264>";

	private static final List<String> cluster1 = Arrays.asList(new String[] {"席", "欠", "休", "出", "コロシアム"});

	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event){

		if (event == null || event.getMessage() == null) {
			return;
		}

		String content = event.getMessage().getContent();
		if (StringUtil.isBlank(content)) {
			return;
		}

		if (!content.contains(BOT_PREFIX)) {
			// 雑談割り込み必要なら
			return;
		}

		LOGGER.info(content);

		int cluster = getCluster(content);
		switch (cluster) {
		case 1:
			return;
		case 2:
			return;
		default:
			// 雑談割り込み必要なら
			return;
		}
	}

	private int getCluster(String content) {

		for (String str : cluster1) {
			if (content.contains(str)) {
				return 1;
			}
		}

		return 0;
	}
}
