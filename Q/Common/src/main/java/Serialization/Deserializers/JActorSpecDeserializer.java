package Serialization.Deserializers;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;

import Action.ExchangeAction;
import Action.IAction;
import Action.PlaceAction;
import Map.Coord;
import Map.IMap;
import Map.GameMap;
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
			return new ExceptionPlayer(name, strategy, exn);
		} else if (jsonArray.size() == 4) {
			String cheat = jsonArray.get(3).getAsString();
			return new CheatingPlayer(name, strategy, cheat);
		}

		throw new RuntimeException("Bad JActorSpecA schema");
	}
}

class ExceptionPlayer extends Player {
	private final String exn;

	public ExceptionPlayer(String name, IStrategy strategy, String exn) {
		super(name, strategy);
		this.exn = exn;
	}

	@Override
	public void setup(IShareableInfo map, List<ITile> tiles) {
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
	private final Cheat cheat;

	public CheatingPlayer(String name, IStrategy strategy, String cheat) {
		super(name, strategy);
		switch (cheat) {
			case "non-adjacent-coordinate":
				this.cheat = new NonAdjacentCoordinate();
				break;
			case "tile-not-owned":
				this.cheat = new TileNotOwned();
				break;
			case "not-a-line":
				this.cheat = new NotALine();
				break;
			case "bad-ask-for-tiles":
				this.cheat = new BadAskForTiles();
				break;
			case "no-fit":
				this.cheat = new NoFit();
				break;
			default:
				throw new RuntimeException("Unknown cheat: " + cheat);
		}
	}

	@Override
	public IAction takeAction(IShareableInfo publicState) {
		Optional<IAction> cheatAction = cheat.tryCheat(publicState);
		return cheatAction.orElseGet(() -> super.takeAction(publicState));
	}

	interface Cheat {
		/**
		 * If it is possible to cheat, returns an illegal action. If cheating is not
		 * possible, returns Optional.empty().
		 *
		 * @param publicState The public state of the game.
		 * @return An illegal action if cheating is possible, Optional.empty() otherwise.
		 */
		Optional<IAction> tryCheat(IShareableInfo publicState);
	}

	class NonAdjacentCoordinate implements Cheat {
		@Override
		public Optional<IAction> tryCheat(IShareableInfo publicState) {
			Coord rightmost = publicState.getMap().getMap().keySet().stream()
					.max(Comparator.comparingInt(Coord::getX)).get();

			return Optional.of(new PlaceAction(List.of(new AbstractMap.SimpleEntry<>(
					new Coord(rightmost.getX() + 1, rightmost.getY()),
					tiles.get(0)
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

	class NotALine implements Cheat {
		@Override
		public Optional<IAction> tryCheat(IShareableInfo publicState) {
			for (ITile tile : tiles) {
				for (Coord validSpot : publicState.getMap().validSpots(tile)) {
					List<ITile> newHand = new ArrayList<>(tiles);
					newHand.remove(tile);
					IMap newMap = publicState.getMap().copyMap();
					var recursiveResult =
							findTilesNotInLine(
									List.of(new AbstractMap.SimpleEntry<>(validSpot, tile)), newMap, newHand
							);
					if (recursiveResult.isPresent()) {
						return recursiveResult.map(PlaceAction::new);
					}
				}
			}
			return Optional.empty();
		}

		private Optional<List<Map.Entry<Coord, ITile>>> findTilesNotInLine(
				List<Map.Entry<Coord, ITile>> placementsSoFar,
				IMap map,
				List<ITile> hand
		) {
			for (ITile tile : hand) {
				for (Coord validSpot : map.validSpots(tile)) {
					var updatedPlacements = new ArrayList<>(placementsSoFar);
					updatedPlacements.add(new AbstractMap.SimpleEntry<>(validSpot, tile));
					if (!sameRowOrColumn(
							updatedPlacements.stream().map(Map.Entry::getKey).collect(Collectors.toList()))
					) {
						return Optional.of(updatedPlacements);
					} else {
						List<ITile> newHand = new ArrayList<>(hand);
						newHand.remove(tile);
						IMap newMap = map.copyMap();
						var recursiveResult =
								findTilesNotInLine(updatedPlacements, newMap, newHand);
						if (recursiveResult.isPresent()) {
							return recursiveResult;
						}
					}
				}
			}
			return Optional.empty();
		}


		private boolean sameRowOrColumn(List<Coord> coords) {
			long distinctXsCount = coords.stream().map(Coord::getX).distinct().count();
			long distinctYsCount = coords.stream().map(Coord::getY).distinct().count();

			return distinctXsCount == 1 || distinctYsCount == 1;
		}
	}

	class BadAskForTiles implements Cheat {
		@Override
		public Optional<IAction> tryCheat(IShareableInfo publicState) {
			if (publicState.getRefTileCount() < tiles.size()) {
				return Optional.of(new ExchangeAction());
			} else {
				return Optional.empty();
			}
		}
	}

	class NoFit implements Cheat {
		@Override
		public Optional<IAction> tryCheat(IShareableInfo publicState) {
			var board = new GameMap(publicState.getMap().getMap()) {
				public Set<Coord> invalidSpots(ITile tile) {
					return frontier().stream()
							.filter((c) -> !checkNeighboringTiles(c, tile))
							.collect(Collectors.toSet());
				}
			};

			for (var tile : tiles) {
				for (Coord invalidSpot : board.invalidSpots(tile)) {
					return Optional.of(new PlaceAction(List.of(new AbstractMap.SimpleEntry<>(
							invalidSpot,
							tile
					))));
				}
			}

			return Optional.empty();
		}
	}
}