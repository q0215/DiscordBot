package me.q9029.discord.bot;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.jetty.util.StringUtil;

import me.q9029.discord.bot.service.AbsentManageService;
import me.q9029.discord.bot.service.impl.AbsentManageServiceImpl;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class BotEvents {

	public static final String BOT_PREFIX = "<@380778136431755264>";

	private static Date createdDate = new Date();

	private static AbsentManageService absentManageService = new AbsentManageServiceImpl();

	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event){

		// バグ回避
		if (event == null) {
			return;
		}
		if (event.getMessage() == null) {
			return;
		}

		// 対象外終了
		String content = event.getMessage().getContent();
		if (!content.startsWith(BOT_PREFIX)) {
			return;
		}

		// 日付変更による登録除去
		Date currentDate = new Date();
		if (!DateUtils.isSameDay(createdDate, currentDate)) {
			createdDate = currentDate;
			absentManageService.clear(event.getChannel().getLongID());
		}

		// 出席確認
		if(content.startsWith(BOT_PREFIX + "欠席予定")) {

			List<Long> list = absentManageService.getAbsentUserList(event.getChannel().getLongID());

			if (list == null || list.size() <= 0) {
				DiscordUtils.sendMessage(event.getChannel(), "欠席予定はいないゾ☆");
				return;
			}

			StringBuilder messageBuilder = new StringBuilder();
			IDiscordClient cli = DiscordClientUtil.getBuiltClient();
			for (Long userId : list) {
				IUser user2 = cli.fetchUser(userId);
				String name = user2.getNicknameForGuild(event.getGuild());

				if (StringUtil.isBlank(name)) {
					name = user2.getName();
				}
				messageBuilder.append(name).append("さん ");
			}
			messageBuilder.append("が欠席予定だゾ☆");

			DiscordUtils.sendMessage(event.getChannel(), messageBuilder.toString());
			return;
		}

		// コメント
		Long autherId = event.getAuthor().getLongID();
		IUser user = event.getAuthor();
		String name = user.getNicknameForGuild(event.getGuild());

		if (StringUtil.isBlank(name)) {
			name = user.getName();
		}

		// 出席
		if(content.startsWith(BOT_PREFIX + "出席")) {
			int result = absentManageService.attend(event.getChannel().getLongID(), autherId);
			if (result == 0) {
				DiscordUtils.sendMessage(event.getChannel(), "欠席予定を取り消すゾ☆");
			} else {
				DiscordUtils.sendMessage(event.getChannel(), "欠席予定で登録されてないゾ☆");
			}
			return;
		}

		// 欠席
		if(content.startsWith(BOT_PREFIX + "欠席")) {
			int result = absentManageService.absent(event.getChannel().getLongID(), autherId);
			if (result == 0) {
				DiscordUtils.sendMessage(event.getChannel(), "欠席予定で登録しておくゾ☆");
			} else {
				DiscordUtils.sendMessage(event.getChannel(), "もう欠席予定で登録してあるゾ☆");
			}
			return;
		}
	}
}
