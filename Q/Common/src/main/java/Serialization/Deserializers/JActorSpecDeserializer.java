package Serialization.Deserializers;

import java.lang.reflect.Type;

import Serialization.JActorSpec;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JActorSpecDeserializer implements JsonDeserializer<JActorSpec> {
	@Override
	public JActorSpec deserialize(JsonElement jsonElement, Type type,
																JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException {
		Gson gson = new Gson();

		JsonArray jsonArray = jsonElement.getAsJsonArray();

		String name = jsonArray.get(0).getAsString();
		String strategy = jsonArray.get(1).getAsString();
		String exn = null;
		if (jsonArray.size() > 2) {
			exn = jsonArray.get(2).getAsString();
		}

		return new JActorSpec(name, strategy, exn);
	}
}
