package me.q9029.discord.bot.event.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.q9029.discord.bot.event.AbsentManageService;

public class AbsentManageServiceImpl implements AbsentManageService {

	private static final Map<Long, List<Long>> absentManageMap = new HashMap<>();

	@Override
	public int clear(long channelId) {
		absentManageMap.put(channelId, new ArrayList<>());
		return 0;
	}

	@Override
	public int attend(long channelId, long autherId) {
		List<Long> userList = absentManageMap.get(channelId);

		if (userList == null) {
			return 1;
		}

		if (!userList.contains(autherId)) {
			return 1;
		}

		userList.remove(autherId);
		return 0;
	}

	@Override
	public int absent(long channelId, long autherId) {
		List<Long> userList = absentManageMap.get(channelId);

		if (userList == null) {
			userList = new ArrayList<>();
		}

		if (userList.contains(autherId)) {
			return 1;
		}

		userList.add(autherId);
		absentManageMap.put(channelId, userList);
		return 0;
	}

	@Override
	public List<Long> getAbsentUserList(long channelId) {
		return absentManageMap.get(channelId);
	}
}
