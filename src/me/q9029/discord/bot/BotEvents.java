package me.q9029.discord.bot;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import me.q9029.discord.bot.event.AbsentManageService;
import me.q9029.discord.bot.event.impl.AbsentManageServiceImpl;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class BotEvents {

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
		if (!content.startsWith(DiscordUtils.BOT_PREFIX)) {
			return;
		}

		// 日付変更による登録除去
		Date currentDate = new Date();
		if (!DateUtils.isSameDay(createdDate, currentDate)) {
			createdDate = currentDate;
			absentManageService.clear(event.getChannel().getLongID());
		}

		// 出席確認
		if(content.startsWith(DiscordUtils.BOT_PREFIX + "欠席確認")) {

			List<Long> list = absentManageService.getAbsentUserList(event.getChannel().getLongID());

			if (list == null || list.size() <= 0) {
				DiscordUtils.sendMessage(event.getChannel(), "欠席する人はいないみたいだよ！");
				return;
			}

			StringBuilder messageBuilder = new StringBuilder().append("欠席：");
			IDiscordClient cli = DiscordClientUtil.getBuiltClient();
			for (Long userId : list) {
				IUser user2 = cli.fetchUser(userId);
				String nickName = user2.getNicknameForGuild(event.getGuild());
				String userName = user2.getName();
				messageBuilder.append(nickName).append("(").append(userName).append(") ");
			}

			DiscordUtils.sendMessage(event.getChannel(), messageBuilder.toString());
			return;
		}

		// 欠席
		if(content.startsWith(DiscordUtils.BOT_PREFIX + "欠席連絡")) {
			Long id = event.getAuthor().getLongID();
			int result = absentManageService.absent(event.getChannel().getLongID(), id);
			if (result == 0) {
				DiscordUtils.sendMessage(event.getChannel(), "欠席の連絡を受け付けたよ");
			} else {
				DiscordUtils.sendMessage(event.getChannel(), "すでに欠席で登録されてるよ");
			}
			return;
		}

		// 出席
		if(content.startsWith(DiscordUtils.BOT_PREFIX + "欠席取消")) {
			Long id = event.getAuthor().getLongID();
			int result = absentManageService.attend(event.getChannel().getLongID(), id);
			if (result == 0) {
				DiscordUtils.sendMessage(event.getChannel(), "欠席の登録を解除したよ！");
			} else {
				DiscordUtils.sendMessage(event.getChannel(), "欠席の連絡は受けてなかったみたいだよ");
			}
			return;
		}
	}
}
