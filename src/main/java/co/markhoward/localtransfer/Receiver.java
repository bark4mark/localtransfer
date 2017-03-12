package co.markhoward.localtransfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Receiver implements Runnable {
	private final int port;
	private final ObjectMapper objectMapper;
	public Receiver (final int port, final ObjectMapper objectMapper){
		this.port = port;
		this.objectMapper = objectMapper;
	}
	
	@Override
	public void run (){
		try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
			while (true) {
				byte[] buffer = new byte[1024];
				DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
				datagramSocket.receive(datagramPacket);
				String received = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
				Beat beat = objectMapper.readValue(received, Beat.class);
				logger.debug("Received: {}", beat);
			}
		} catch (IOException exception) {
			logger.error("An I/O error has occurred", exception);
		}
	}
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
}
