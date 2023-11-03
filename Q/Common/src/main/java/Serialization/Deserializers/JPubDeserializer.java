package Serialization.Deserializers;

import java.lang.reflect.Type;

import Serialization.JMap;
import Serialization.JPlayer;
import Serialization.JPub;
import Serialization.JRow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class JPubDeserializer implements JsonDeserializer<JPub> {
	@Override
	public JPub deserialize(JsonElement jsonElement, Type type,
													JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(JRow.class, new JRowDeserializer())
				.create();

		JsonObject jsonObject = jsonElement.getAsJsonObject();

		JRow[] jRows = gson.fromJson(jsonObject.get("map"), JRow[].class);
		JMap jMap = new JMap(jRows);

		int tileCount = jsonObject.get("tile*").getAsInt();

		JsonArray jsonArray = jsonObject.get("players").getAsJsonArray();

		JPlayer activePlayer = gson.fromJson(jsonArray.remove(0), JPlayer.class);
		int[] otherScores = gson.fromJson(jsonArray, int[].class);

		return new JPub(jMap, tileCount, activePlayer, otherScores);
	}
}
