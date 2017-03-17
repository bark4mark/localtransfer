package co.markhoward.localtransfer.heartbeat;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude={"date"})
public class Peer {
	private String ipAddress;
	private int port;
	private Date date;
}
