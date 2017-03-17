package co.markhoward.localtransfer.heartbeat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.markhoward.localtransfer.NetworkInterfaceUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Heartbeat extends TimerTask {
	private final int port;
	private final ObjectMapper objectMapper;

	@Override
	public void run() {
		for(NetworkInterface networkInterface: NetworkInterfaceUtils.localNetworkInterfaces()) {
			List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
			sendMessages(addresses);
		}
	}

	private void sendMessages(final List<InterfaceAddress> addresses){
		for(InterfaceAddress address: addresses){
			if(address.getBroadcast() == null)
				continue;
			InetAddress iNetAddress = address.getAddress();
			if(iNetAddress == null)
				continue;
			Date now = new Date ();
			Peer beat = new Peer(iNetAddress.getHostAddress(), port, now);
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
