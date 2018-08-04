package me.q9029.discord.app;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.common.DiscordProps;
import me.q9029.discord.app.util.DiscordPropsUtil;

/**
 * アプリケーションの開始および終了の起点となるクラス。
 * 
 * @author q9029
 */
public class DiscordAppMain {

	private static Logger logger = LoggerFactory.getLogger(DiscordAppMain.class);

	public static void main(String[] args) {

		// 開始ログ
		logger.info("Start.");

		try {
			// プロセスファイルの作成
			File procFile = new File(DiscordPropsUtil.getString(DiscordProps.PROC_FILE));
			if (!procFile.createNewFile()) {
				throw new RuntimeException("Other processes exist.");
			}

			try {
				// 処理スレッドの開始
				DiscordClientThread thread = DiscordClientThread.getInstance();
				thread.start();

				// プロセスファイルの存在監視
				// スレッドの生存監視
				while (procFile.exists() && thread.isAlive()) {
					Thread.sleep(1000);
				}

				// 処理スレッドの中断
				thread.interrupt();

				// 処理スレッドの終了待機
				thread.join();

			} finally {
				// プロセスファイルの削除
				if (procFile.exists()) {
					procFile.delete();
				}
			}
		} catch (Exception e) {
			logger.error("An unexpected exception occurred.", e);
		}

		// 終了ログ
		logger.info("End.");

		// プロセス終了
		System.exit(0);
	}
}
