package infoassembly.test.common;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter {
	public synchronized String format(LogRecord record) {
		return record.getMessage() + "\n";
	}
}
