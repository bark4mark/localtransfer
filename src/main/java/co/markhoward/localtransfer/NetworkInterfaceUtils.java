package co.markhoward.localtransfer;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkInterfaceUtils {
	private static List<NetworkInterface> networkInterfaces = null;
	public static List<NetworkInterface> localNetworkInterfaces (){
		if(networkInterfaces == null) {
			networkInterfaces = new LinkedList<> ();
			try {
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				while (interfaces.hasMoreElements()) {
					NetworkInterface networkInterface = interfaces.nextElement();
					if (networkInterface.isLoopback() || !networkInterface.isUp())
						continue;
					networkInterfaces.add(networkInterface);
				}
			} catch (SocketException exception) {
				logger.error("An I/O error has occurred", exception);
			}
		}
		return networkInterfaces;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(NetworkInterfaceUtils.class);
}
