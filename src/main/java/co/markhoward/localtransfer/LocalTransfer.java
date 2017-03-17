package co.markhoward.localtransfer;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import co.markhoward.localtransfer.heartbeat.Heartbeat;
import co.markhoward.localtransfer.heartbeat.Peer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LocalTransfer {
	private static final int interval = 5000;
	private final Set<Peer> peers = Sets.newConcurrentHashSet();

	public static void main(String[] args) {
		LocalTransfer localTransfer = new LocalTransfer();
		localTransfer.run();
	}

	public void run() {
		ObjectMapper objectMapper = new ObjectMapper();
		int heartbeatPort = 65100;
		int fileSendingPort = 65101;

		ExecutorService service = Executors.newFixedThreadPool(2);
		Receiver receiver = new Receiver(heartbeatPort, objectMapper, peers);
		service.execute(receiver);

		Heartbeat heartbeat = new Heartbeat(heartbeatPort, objectMapper);
		Timer timer = new Timer();
		Date now = new Date();
		timer.schedule(heartbeat, now, interval);

		PeerManager peerManager = new PeerManager(peers);
		Timer peerTimer = new Timer();
		peerTimer.schedule(peerManager, now, interval);

		FileService fileService = new FileService(fileSendingPort, peers, objectMapper);
		fileService.setupServer();
		
		openBrowser(fileSendingPort);
	}

	private void openBrowser(int fileSendingPort) {
		String fileSharingURL = String.format("http://localhost:%d", fileSendingPort);
		try {
			if (Desktop.isDesktopSupported())
				Desktop.getDesktop().browse(new URI(fileSharingURL));
		} catch (IOException | URISyntaxException exception) {
			logger.error("Cannot open default browser");
		}
		logger.info("Open {} in your browser if it did not open automatically", fileSharingURL);
	}

	private final static Logger logger = LoggerFactory.getLogger(LocalTransfer.class);

}
