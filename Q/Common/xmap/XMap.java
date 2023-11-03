import Serialization.Deserializers.JRowDeserializer;
import Serialization.JCoordinate;
import Serialization.JMap;
import Serialization.JRow;
import Serialization.JTile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Set;

import Map.Coord;
import Map.IMap;
import Map.Tile.ITile;

public class XMap {
	public static void main(String[] args) throws IOException {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(JRow.class, new JRowDeserializer())
				.create();
		JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));

		JRow[] jRows = gson.fromJson(parser.next(), JRow[].class);
		JMap jMap = new JMap(jRows);
		IMap gameMap = jMap.convert();

		JTile jTile = gson.fromJson(parser.next(), JTile.class);
		ITile tile = jTile.convert();

		Set<Coord> validSpots = gameMap.validSpots(tile);

		System.out.println(gson.toJson(validSpots.stream()
				.map((coord) -> new JCoordinate(coord.getX(), coord.getY()))
				.sorted().toArray(), JCoordinate[].class));
	}
}
