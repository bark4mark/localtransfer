package co.markhoward.localtransfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Heartbeat extends TimerTask {
	private final int port;
	private final ObjectMapper objectMapper;
	public Heartbeat(final int port, final ObjectMapper objectMapper) {
		this.port = port;
		this.objectMapper = objectMapper;
	}

	@Override
	public void run() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback() || !networkInterface.isUp())
					continue;
				logger.debug("Found interface which is neither a loopback or down: {}", networkInterface.getName());
				List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
				sendMessages(addresses);
			}
		} catch (SocketException exception) {
			logger.error("An I/O error has occurred", exception);
		}
	}
	
	private void sendMessages(final List<InterfaceAddress> addresses){
		for(InterfaceAddress address: addresses){
			if(address.getBroadcast() == null)
				continue;
			Beat beat = new Beat(address.toString(), port);
			try {
				String beatMessage = objectMapper.writeValueAsString(beat);
				byte[] message = beatMessage.getBytes();
				DatagramPacket datagramPacket = new DatagramPacket(message, message.length, address.getBroadcast(), port);
				try(DatagramSocket dsocket = new DatagramSocket()){
					dsocket.send(datagramPacket);
				}
			} catch (IOException exception) {
				logger.error("The socket could not be opened", exception);
			}
			
		}
	}
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
}
