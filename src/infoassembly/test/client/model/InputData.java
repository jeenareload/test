package infoassembly.test.client.model;

import infoassembly.test.common.Info;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;


public class InputData {

	private FileReader _fileReader;
	private BufferedReader _buffer;
	private static InputData _input;
	private static int  count=0;

	private InputData() throws IOException {
		init();
	}

	public synchronized ArticalInfo getNextArtical() {
		if (_buffer != null) {
			try {
				String str = null;
				do {
					str = _buffer.readLine();
					if (str != null && str.length() > 5) {
						try {
							JSONObject json = new JSONObject(str);
							if (json.has("description")) {
								return new ArticalInfo(json.get("description").toString(), ++count);
							}
						} catch (Exception e) {
						}
					}
				} while (str != null);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		closeConnection();
		return null;
	}

	private void closeConnection() {
		if (_buffer != null) {
			try {
				_buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (_fileReader != null) {
			try {
				_fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void init() throws IOException {
		File file = new File(Info.INPUT_FILE_NAME);
		if (file.exists() && file.isFile()) {
			_fileReader = new FileReader(file);
			_buffer = new BufferedReader(_fileReader);
		}
	}

	public static synchronized InputData getInstance() throws Exception {
		if (_input == null) {
			_input = new InputData();
		}
		return _input;
	}
}
