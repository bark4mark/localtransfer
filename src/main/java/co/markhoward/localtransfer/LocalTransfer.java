package co.markhoward.localtransfer;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LocalTransfer {
	private static final int interval = 5000;

	public static void main(String[] args) {
		LocalTransfer localTransfer = new LocalTransfer ();
		localTransfer.run();
	}
	
	public void run (){
		ObjectMapper objectMapper = new ObjectMapper ();
		int port = 65100;
		
		ExecutorService service = Executors.newFixedThreadPool(1);
		Receiver receiver = new Receiver(port, objectMapper);
		service.execute(receiver);
		
		Heartbeat heartbeat = new Heartbeat(port, objectMapper);
		Timer timer = new Timer();
		Date now = new Date();
		timer.schedule(heartbeat, now, interval);
	}

}
