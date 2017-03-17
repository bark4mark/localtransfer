package co.markhoward.localtransfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.markhoward.localtransfer.heartbeat.Peer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Receiver implements Runnable {
	private final int port;
	private final ObjectMapper objectMapper;
	private final Set<Peer> found;
	
	@Override
	public void run (){
		try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
			while (true) {
				byte[] buffer = new byte[1024];
				DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
				datagramSocket.receive(datagramPacket);
				String received = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
				Peer beat = objectMapper.readValue(received, Peer.class);
				if(isLocal(beat))
					continue;
				found.add(beat);
				logger.debug("Received: {}", beat);
			}
		} catch (IOException exception) {
			logger.error("An I/O error has occurred", exception);
		}
	}
	
	private boolean isLocal(final Peer beat) {
		for(NetworkInterface networkInterface: NetworkInterfaceUtils.localNetworkInterfaces()) {
			List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
			for(InterfaceAddress address: addresses){
				InetAddress iNetAddress = address.getAddress();
				if(iNetAddress == null)
					continue;
				if(beat.getIpAddress().equals(iNetAddress.getHostAddress()))
					return true;
			}
		}
		return false;
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
}
