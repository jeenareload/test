package infoassembly.test.client;

import infoassembly.test.client.model.ArticalInfo;
import infoassembly.test.client.model.InputData;
import infoassembly.test.common.Info;
import infoassembly.test.common.LoggerFac;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanJobEvent;
import org.gearman.GearmanJobReturn;
import org.gearman.GearmanServer;


public class Client implements Runnable {

	private long initialTime = System.currentTimeMillis();
	private long endTime = 0;
	private long startTime = 0;

	public Client() {

	}
	public static void main(String[] args) {
		for (int index = 0; index < Info.MAX_THREAD; index++) {
			new Thread(new Client()).start();
		}
	}
	@Override
	public void run() {

		/*
		 * Create a Gearman instance
		 */
		Gearman gearman = Gearman.createGearman();

		/*
		 * Create a new gearman client.
		 * 
		 * The client is used to submit requests the job server.
		 */
		GearmanClient client = gearman.createGearmanClient();

		/*
		 * Create the job server object. This call creates an object represents
		 * a remote job server.
		 * 
		 * Parameter 1: the host address of the job server. Parameter 2: the
		 * port number the job server is listening on.
		 * 
		 * A job server receives jobs from clients and distributes them to
		 * registered workers.
		 */
		GearmanServer server = gearman.createGearmanServer(Info.HOST, Info.PORT);

		/*
		 * Tell the client that it may connect to this server when submitting
		 * jobs.
		 */
		client.addServer(server);

		/*
		 * Submit a job to a job server.
		 * 
		 * Parameter 1: the gearman function name Parameter 2: the data passed
		 * to the server and worker
		 * 
		 * The GearmanJobReturn is used to poll the job's result
		 */
		try {
			ArticalInfo arical = InputData.getInstance().getNextArtical();
			while (arical != null) {
				if (!performTaskOnSentence(client, arical)) {
					performTaskOnSentence(client, arical);
				}
				arical = InputData.getInstance().getNextArtical();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		gearman.shutdown();
	}

	private boolean performTaskOnSentence(GearmanClient client, ArticalInfo artical) throws InterruptedException {

		startTime = System.currentTimeMillis();

		GearmanJobReturn jobReturn = client.submitJob(Info.FUNCTION_NAME, (artical.getText()).getBytes());

		while (!jobReturn.isEOF()) {

			/*
			 * Submit a job to a job server.
			 * 
			 * Parameter 1: the gearman function name Parameter 2: the data
			 * passed to the server and worker
			 * 
			 * The GearmanJobReturn is used to poll the job's result
			 */

			// Poll the next job event (blocking operation)
			GearmanJobEvent event = jobReturn.poll();

			switch (event.getEventType()) {

			// success
			case GEARMAN_JOB_SUCCESS: // Job completed successfully
				// print the result
				endTime = System.currentTimeMillis();
				System.out.println(new String("Artical ID: " + artical.getArticalID() + " Start Time =" + convertTimeToStringFormate(startTime - initialTime) + "End Time " + convertTimeToStringFormate(endTime - initialTime) + "  " + Thread.currentThread().getName() +" "+ new String(event.getData())));
				LoggerFac.logMessage(new String("Artical ID: " + artical.getArticalID() + " Start Time =" + convertTimeToStringFormate(startTime - initialTime) + "End Time " + convertTimeToStringFormate(endTime - initialTime) + "  " + Thread.currentThread().getName() +" "+ new String(event.getData())));
//				System.out.println(new String(event.getData()));
				return true;

			// failure
			case GEARMAN_SUBMIT_FAIL: // The job submit operation failed
			case GEARMAN_JOB_FAIL: // The job's execution failed
				System.err.println(event.getEventType() + ": " + new String(event.getData()));
				LoggerFac.logMessage(event.getEventType() + ": " + new String(event.getData()));
				return false;
			default:
			}
		}
		return true;
	}

	private static String convertTimeToStringFormate(long time) {
		Format format = new SimpleDateFormat("HH:mm:ss.SSS");
		return format.format(new Date(time)).toString();
	}
}
