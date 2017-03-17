package co.markhoward.localtransfer;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import spark.ResponseTransformer;
@AllArgsConstructor
public class JacksonTransformer implements ResponseTransformer {
	private final ObjectMapper objectMapper;

	@Override
	public String render(Object object) throws Exception {
		return objectMapper.writeValueAsString(object);
	}

}
