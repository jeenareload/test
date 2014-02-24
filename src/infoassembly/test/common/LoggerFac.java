package infoassembly.test.common;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFac {

	private static Logger log;

	static {
		initLogger();
	}

	public synchronized static void logMessage(String message) {
		if (log != null) {
			log.log(Level.INFO, message);
		}
	}

	public static void main(String[] args) {
		logMessage("HELLO");
	}
	private static void initLogger() {
		log = Logger.getLogger("Logger.");
		try {
			
			FileHandler file = new FileHandler(Info.OUT_LOG_FILE);
			file.setFormatter(new LogFormatter());
			log.addHandler(file);
			log.setUseParentHandlers(false);
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
