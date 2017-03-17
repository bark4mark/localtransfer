package co.markhoward.localtransfer;

import java.util.Set;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.markhoward.localtransfer.heartbeat.Peer;
import lombok.AllArgsConstructor;
import spark.Spark;

@AllArgsConstructor
public class FileService {
	private final int port;
	private final Set<Peer> peers;
	private final ObjectMapper objectMapper;
	
	public void setupServer (){
		Spark.staticFileLocation("/shared");
		Spark.port(port);
		JacksonTransformer transformer = new JacksonTransformer(objectMapper);
		Spark.get("/file", (req, res) -> {
			return "Hello";
		}, transformer);
		
		Spark.post("/files", (request, response) -> {
			return "Hello";
		});
		
		Spark.get("/quit", (request, response) -> {
			System.exit(0);
			return null;
		});
		
		Spark.get("/", (request, response) -> {
			JtwigTemplate template = JtwigTemplate.classpathTemplate("shared/main.html");
			JtwigModel model = JtwigModel.newModel();
			return template.render(model);
		});
	}

}
