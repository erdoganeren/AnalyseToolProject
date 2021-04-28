/*
 * SonarScanner CLI
 * Copyright (C) 2011-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package pacApp.pacSonarScanner;
/*
 * SonarScanner CLI
 * Copyright (C) 2011-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.sonarsource.scanner.api.EmbeddedScanner;
import org.sonarsource.scanner.api.ScanProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import pacApp.pacComponent.MessageComponent;
import pacApp.pacService.FileService;

/**
 * Arguments :
 * <ul>
 * <li>scanner.home: optional path to Scanner home (root directory with
 * sub-directories bin, lib and conf)</li>
 * <li>scanner.settings: optional path to runner global settings, usually
 * ${scanner.home}/conf/sonar-scanner.properties. This property is used only if
 * ${scanner.home} is not defined</li>
 * <li>project.home: path to project root directory. If not set, then it's
 * supposed to be the directory where the runner is executed</li>
 * <li>project.settings: optional path to project settings. Default value is
 * ${project.home}/sonar-project.properties.</li>
 * </ul>
 *
 * @since 1.0
 */


@Service
@Configurable
public class Main {
	
	private static final String SEPARATOR = "------------------------------------------------------------------------";
	private final Exit exit;
	private final Cli cli;
	private final Conf conf;
	private EmbeddedScanner runner;
	private ScannerFactory runnerFactory;
	private Logs logger;

	@Autowired
	FileService fileService;
	
	@Autowired
	MessageComponent messageComp;
	
	Main() {
		this.exit = null;
		this.cli = null;
		this.conf = null;
	}
	
	Main(Exit exit, Cli cli, Conf conf, ScannerFactory runnerFactory, Logs logger) {
		this.exit = exit;
		this.cli = cli;
		this.conf = conf;
		this.runnerFactory = runnerFactory;
		this.logger = logger;
	}

	/**
	 * Only for test
	 */
	public static void main(String[] args) {
		// execute("C:\\Users\\eeren\\git\\dataWarehouseProject\\dataWarehouseProject_Core",
		// "my:project3");
		// execute(Paths.get("C:\\Users\\eeren\\git\\TestPoj").toString(),
		// "my:project11");
		new Main().execute(Paths.get("C:\\Users\\eeren\\git\\TestPoj").toString(),"my:project1");
	}

	public HashMap<Integer, String> execute(String path, String projectKey) {
		Logs logs = new Logs(System.out, System.err);
		Exit exit = new Exit();
		Cli cli = new Cli(exit, logs).parse(new String[0]);
		Main main = new Main(exit, cli, new Conf(cli, logs, System.getenv()), new ScannerFactory(logs), logs);
		return main.executeInternal(path, projectKey);
	}

	private HashMap<Integer, String> executeInternal(String path, String projectKey) {
		Stats stats = new Stats(logger).start();
		HashMap<Integer, String> messageStatus = new HashMap();
		int status = Exit.INTERNAL_ERROR;
		try {
			Properties p = conf.properties();
			p.put("sonar.host.url", "http://localhost:9000");
			p.put("sonar.projectKey", projectKey);
			p.put("sonar.projectBaseDir", path);
			p.put("sonar.java.sources", "src\\main\\java");
			p.put("sonar.java.binaries", "*");
			p.put("sonar.scm.exclusions.disabled","true");
			// p.put("sonar.exclusions","**/2");
			p.put("sonar.login", "admin");
			p.put("sonar.password", "pwd");
			
			checkSkip(p);
			configureLogging(p);
			init(p);
			runner.start();
			logger.info(String.format("Analyzing on %s", conf.isSonarCloud(null) ? "SonarCloud" : ("SonarQube server " + runner.serverVersion())));
			execute(stats, p);
			status = Exit.SUCCESS;
			messageStatus.put(status, "SUCCESS");
			return messageStatus;
		} catch (Throwable e) {
			displayExecutionResult(stats, "FAILURE");
			showError("Error during SonarScanner execution", e, cli.isDebugEnabled());
			status = isUserError(e) ? Exit.USER_ERROR : Exit.INTERNAL_ERROR;
			messageStatus.put(status, e.getMessage());
			return messageStatus;
		} finally {
			// exit.exit(status);
			// FileService.clearTempDir();
		}

	}
	
	
	private void checkSkip(Properties properties) {
		if ("true".equalsIgnoreCase(properties.getProperty(ScanProperties.SKIP))) {
			logger.info("SonarScanner analysis skipped");
			exit.exit(Exit.SUCCESS);
		}
	}

	private void init(Properties p) {
		SystemInfo.print(logger);
		if (cli.isDisplayVersionOnly()) {
			exit.exit(Exit.SUCCESS);
		}

		runner = runnerFactory.create(p, cli.getInvokedFrom());
	}

	private void configureLogging(Properties props) {
		if ("true".equals(props.getProperty("sonar.verbose"))
				|| "DEBUG".equalsIgnoreCase(props.getProperty("sonar.log.level"))
				|| "TRACE".equalsIgnoreCase(props.getProperty("sonar.log.level"))) {
			logger.setDebugEnabled(true);
		}
	}

	private void execute(Stats stats, Properties p) {
		runner.execute((Map) p);
		displayExecutionResult(stats, "SUCCESS");
	}

	private void displayExecutionResult(Stats stats, String resultMsg) {
		logger.info(SEPARATOR);
		logger.info("EXECUTION " + resultMsg);
		logger.info(SEPARATOR);
		stats.stop();
		logger.info(SEPARATOR);
	}

	private void showError(String message, Throwable e, boolean debug) {
		if (debug || !isUserError(e)) {
			logger.error(message, e);
		} else {
			logger.error(message);
			logger.error(e.getMessage());
			String previousMsg = "";
			for (Throwable cause = e.getCause(); cause != null && cause.getMessage() != null
					&& !cause.getMessage().equals(previousMsg); cause = cause.getCause()) {
				logger.error("Caused by: " + cause.getMessage());
				previousMsg = cause.getMessage();
			}
		}

		if (!cli.isDebugEnabled()) {
			logger.error("");
			suggestDebugMode();
		}
	}

	private static boolean isUserError(Throwable e) {
		// class not available at compile time (loaded by isolated classloader)
		return "org.sonar.api.utils.MessageException".equals(e.getClass().getName());
	}

	private void suggestDebugMode() {
		if (!cli.isEmbedded()) {
			logger.error("Re-run SonarScanner using the -X switch to enable full debug logging.");
		}
	}

}
