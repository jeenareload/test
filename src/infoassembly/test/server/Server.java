package infoassembly.test.server;

import infoassembly.test.common.Info;

import java.io.IOException;

import org.gearman.Gearman;


public class Server {
	public static void main(String[] args) throws IOException {
		
		Gearman gearman = Gearman.createGearman();

		try {
			gearman.startGearmanServer(Info.PORT);
			System.out.println("Server Started................................");
		} catch (IOException ioe) {
			gearman.shutdown();
			throw ioe;
		}
	}

}
