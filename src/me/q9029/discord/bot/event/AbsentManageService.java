package me.q9029.discord.bot.event;

import java.util.List;

public interface AbsentManageService {

	int clear(long channelId);

	int attend(long channelId, long autherId);

	int absent(long channelId, long autherId);

	List<Long> getAbsentUserList(long channelId);
}
