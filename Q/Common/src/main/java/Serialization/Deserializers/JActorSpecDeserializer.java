package Serialization.Deserializers;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import Action.IAction;
import Action.PlaceAction;
import Map.Coord;
import Map.IMap;
import Map.Tile.ITile;
import Map.Tile.Tile;
import Player.Player;
import Player.Strategy.DagStrategy;
import Player.Strategy.IStrategy;
import Player.Strategy.LdasgStrategy;
import Referee.IShareableInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JActorSpecDeserializer implements JsonDeserializer<Player> {
	@Override
	public Player deserialize(JsonElement jsonElement, Type type,
																JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException {
		Gson gson = new Gson();

		JsonArray jsonArray = jsonElement.getAsJsonArray();

		String name = jsonArray.get(0).getAsString();
		String jStrategy = jsonArray.get(1).getAsString();

		IStrategy strategy;

		switch (jStrategy) {
			case "dag":
				strategy = new DagStrategy();
				break;
			case "ldasg":
				strategy = new LdasgStrategy();
				break;
			default:
				throw new RuntimeException("Unknown strategy: " + jStrategy);
		}

		if (jsonArray.size() == 2) {
			return new Player(name, strategy);
		} else if (jsonArray.size() == 3) {
			String exn = jsonArray.get(2).getAsString();
		}
	}
}

class ExceptionPlayer extends Player {
	private final String exn;
	public ExceptionPlayer(String name, IStrategy strategy, String exn) {
		super(name, strategy);
		this.exn = exn;
	}

	@Override
	public void setup(IMap map, List<ITile> tiles) {
		if ("setup".equals(exn)) throw new RuntimeException();
		super.setup(map, tiles);
	}

	@Override
	public IAction takeAction(IShareableInfo publicState) {
		if ("take-turn".equals(exn)) throw new RuntimeException();
		return super.takeAction(publicState);
	}

	@Override
	public void newTiles(List<ITile> tiles) {
		if ("new-tiles".equals(exn)) throw new RuntimeException();
		super.newTiles(tiles);
	}

	@Override
	public void win(boolean won) {
		if ("win".equals(exn)) throw new RuntimeException();
		super.win(won);
	}
}

class CheatingPlayer extends Player {
	private final String cheat;

	public CheatingPlayer(String name, IStrategy strategy, String cheat) {
		super(name, strategy);
		this.cheat = cheat;
	}

	interface Cheat {
		Optional<IAction> tryCheat(IShareableInfo publicState);
	}

	class NonAdjacentCoordinate implements Cheat {
		@Override
		public Optional<IAction> tryCheat(IShareableInfo publicState) {
			Coord rightmost = publicState.getMap().getMap().keySet().stream()
					.max(Comparator.comparingInt(Coord::getX)).get();

			return Optional.of(new PlaceAction(List.of(new AbstractMap.SimpleEntry<>(
					new Coord(rightmost.getX() + 1, rightmost.getY()),
					tiles.getFirst()
			))));
		}
	}

	class TileNotOwned implements Cheat {
		@Override
		public Optional<IAction> tryCheat(IShareableInfo publicState) {
			Optional<Tile> tileToPlace = Arrays.stream(ITile.TileColor.values()).flatMap(
					color -> Arrays.stream(ITile.Shape.values()).map(
							shape -> new Tile(color, shape)
					)
			).filter(tile ->
					!tiles.contains(tile) && !publicState.getMap().validSpots(tile).isEmpty()
			).findFirst();

			return tileToPlace.map(tile -> new PlaceAction(List.of(new AbstractMap.SimpleEntry<>(
					publicState.getMap().validSpots(tile).stream().findAny().get(),
					tile
			))));
		}
	}
}