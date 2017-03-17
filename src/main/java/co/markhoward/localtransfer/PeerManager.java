package co.markhoward.localtransfer;

import java.io.IOException;
import java.util.Set;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import co.markhoward.localtransfer.heartbeat.Peer;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@AllArgsConstructor
public class PeerManager extends TimerTask {
	private final Set<Peer> peers;
	private final OkHttpClient client = new OkHttpClient();

	@Override
	public void run() {
		Set<Peer> temp = Sets.newHashSet();
		for(Peer peer: peers){
			String url = String.format("http://%s:%d", peer.getIpAddress(), peer.getPort());
			logger.debug("Checking peer: {}", url);
			Request request = new Request.Builder()
			        .url(url)
			        .build();
			try {
				Response response = client.newCall(request).execute();
				if(response.isSuccessful())
					temp.add(peer);
			} catch (IOException exception) {
				logger.debug("There was an I/O exception", exception);
			}
		}
		peers.clear();
		peers.addAll(temp);
	}
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
}
