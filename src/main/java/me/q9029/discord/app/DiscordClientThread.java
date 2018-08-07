package me.q9029.discord.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.common.DiscordProps;
import me.q9029.discord.app.common.Interruptible;
import me.q9029.discord.app.util.DiscordClientUtil;
import me.q9029.discord.app.util.DiscordPropsUtil;
import sx.blah.discord.api.IDiscordClient;

/**
 * クライアントのカスタマイズおよびカスタマイズ後の監視を行うクラス。
 * 
 * @author q9029
 */
public class DiscordClientThread extends Thread {

	private static Logger logger = LoggerFactory.getLogger(DiscordClientThread.class);

	private static DiscordClientThread singleton = new DiscordClientThread();

	private boolean isInterrupted = false;

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

		IDiscordClient client = DiscordClientUtil.getInstance();
		try {
			// ログイン
			client.login();

			// スレッドの開始
			String threads = DiscordPropsUtil.getString(DiscordProps.THREADS);
			if (!StringUtils.isEmpty(threads)) {
				for (String key : threads.split(",")) {
					String className = DiscordPropsUtil.getString(key);
					logger.info(className);
					Class<?> clazz = Class.forName(className);
					Thread thread = (Thread) clazz.newInstance();
					threadList.add(thread);
					thread.start();
				}
			}

			// リスナーの追加
			String listeners = DiscordPropsUtil.getString(DiscordProps.LISTENERS);
			if (!StringUtils.isEmpty(listeners)) {
				for (String key : listeners.split(",")) {
					String className = DiscordPropsUtil.getString(key);
					logger.info(className);
					Class<?> clazz = Class.forName(className);
					Object listener = clazz.newInstance();
					listenerList.add(listener);
					client.getDispatcher().registerListener(listener);
				}
			}

			// クライアントの終了待機
			try {
				while (!isInterrupted) {

					// スレッドの死活監視
					for (Thread thread : threadList) {
						if (!thread.isAlive()) {
							throw new Exception(thread.getClass() + " is not alive.");
						}
					}

					// スリープ処理
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				logger.info("Detected an interruption.", e);

			} catch (Exception e) {
				logger.error("Child thread is not alive.", e);
			}

			// リスナーの登録解除
			for (Object listener : listenerList) {
				client.getDispatcher().unregisterListener(listener);

				if (listener instanceof Interruptible) {
					((Interruptible) listener).interrupt();
				}
			}

			// スレッドの終了処理
			for (Thread thread : threadList) {
				try {
					thread.interrupt();
					thread.join();
				} catch (Exception e) {
					logger.error("Fail to interrupt " + thread.getClass());
				}
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

	@Override
	public void interrupt() {
		super.interrupt();
		isInterrupted = true;
	}
}
