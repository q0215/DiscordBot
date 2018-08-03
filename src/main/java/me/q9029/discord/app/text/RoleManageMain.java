package me.q9029.discord.app.text;

import java.io.File;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.q9029.discord.app.common.BundleConst;
import me.q9029.discord.app.common.ClientUtil;
import sx.blah.discord.api.IDiscordClient;

@Deprecated
public class RoleManageMain {

	private static Logger logger = LoggerFactory.getLogger(RoleManageMain.class);

	private static IDiscordClient client = null;

	public static void main(String[] args) {

		int exitCode = 0;
		try {
			logger.info("Start.");

			// get token
			ResourceBundle bundle = ResourceBundle.getBundle(BundleConst.BASE_NAME);
			String token = bundle.getString(BundleConst.TOKEN);

			try {
				// create built client
				client = ClientUtil.getBuiltClient(token);

				// add listener
				RoleManageLinstener listener = new RoleManageLinstener();
				client.getDispatcher().registerListener(listener);

				// client login
				client.login();

				// wait for establishing connection
				long timeoutMillis = Long.parseLong(bundle.getString(BundleConst.TIMEOUT_SEC)) * 1000000000;
				long startReadyMillis = System.nanoTime();
				while (!client.isReady()) {
					if (System.nanoTime() - startReadyMillis >= timeoutMillis) {
						throw new RuntimeException("The waiting time for establishing a connection has been exceeded.");
					}
				}

				File procFile = new File(bundle.getString(BundleConst.PATH_PROC_FILE));
				if (procFile.createNewFile()) {
					while (procFile.exists()) {
						Thread.sleep(1000);
					}
				}

			} finally {
				// client logout
				if (client != null && client.isLoggedIn()) {
					client.logout();
				}
			}

		} catch (Exception e) {
			exitCode = 1;
			logger.error("An unexpected exception occurred.", e);

		} finally {
			logger.info("End.");
		}

		System.exit(exitCode);
	}
}
