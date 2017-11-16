package me.q9029.discord.bot;

import sx.blah.discord.api.IDiscordClient;

public class BotMain {

	public static void main(String[] args){

		// クライアントの作成
		IDiscordClient cli = DiscordClientUtil.getBuiltClient();
		cli.getDispatcher().registerListener(new BotEvents());
		cli.login();
	}
}
