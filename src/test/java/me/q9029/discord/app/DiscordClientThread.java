package me.q9029.discord.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.common.DiscordClientUtil;
import me.q9029.discord.app.common.DiscordPropsUtil;
import sx.blah.discord.api.IDiscordClient;

/**
 * クライアントのカスタマイズおよびカスタマイズ後の監視を行うクラス。
 * 
 * @author q9029
 */
public class DiscordClientThread extends Thread {

	private static Logger logger = LoggerFactory.getLogger(DiscordClientThread.class);

	private static DiscordClientThread singleton = new DiscordClientThread();

	private List<Object> listenerList = new ArrayList<Object>();

	private List<Thread> threadList = new ArrayList<Thread>();

	private DiscordClientThread() {
	}

	public static DiscordClientThread getInstance() {
		return singleton;
	}

	@Override
	public void run() {

		// 開始ログ
		logger.info("Start.");

		IDiscordClient client = DiscordClientUtil.getClient();
		try {
			// ログイン
			client.login();

			// スレッドの開始
			String threadClassName = DiscordPropsUtil.getString(DiscordPropsUtil.Key.THREADS);
			for (String key : threadClassName.split(",")) {
				Class<?> clazz = Class.forName(DiscordPropsUtil.getString(key));
				Thread thread = (Thread) clazz.newInstance();
				thread.start();
			}

			// リスナーの追加
			String className = DiscordPropsUtil.getString(DiscordPropsUtil.Key.LISTENERS);
			for (String key : className.split(",")) {
				Class<?> clazz = Class.forName(DiscordPropsUtil.getString(key));
				Object listener = clazz.newInstance();
				listenerList.add(listener);
				client.getDispatcher().registerListener(listener);
			}

			// クライアントの終了待機
			try {
				while (true) {
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				logger.info("Detected an interruption.");
			}

			// リスナーの登録解除
			for (Object listener : listenerList) {
				client.getDispatcher().unregisterListener(listener);
			}

			// リスナー所有の子スレッドの終了待機

			// スレッドの終了処理
			for (Thread thread : threadList) {
				thread.interrupt();
				thread.join();
			}

		} catch (Exception e) {
			logger.error("An unexpected exception occurred.", e);

		} finally {
			// ログアウト
			if (client.isLoggedIn()) {
				client.logout();
			}
		}

		// 終了ログ
		logger.info("End.");
	}
}
