package co.markhoward.localtransfer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import org.junit.Test;

public class LocalTransferTest {
	@Test
	public void shouldListAllAdapters() throws Exception {
		byte[] message = "Hello".getBytes();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isLoopback() || !networkInterface.isUp())
				continue;
			List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
			for(InterfaceAddress address: addresses){
				if(address.getBroadcast() == null)
					continue;
				DatagramPacket datagramPacket = new DatagramPacket(message, message.length, address.getBroadcast(), 65432);
				try(DatagramSocket dsocket = new DatagramSocket()){
					dsocket.send(datagramPacket);
				}
			}
		}
	}
}
