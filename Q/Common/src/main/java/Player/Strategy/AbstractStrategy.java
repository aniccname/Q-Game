package Player.Strategy;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import Action.ExchangeAction;
import Action.IAction;
import Action.PassAction;
import Action.PlaceAction;
import Map.Coord;
import Map.IMap;
import Map.Tile.ITile;
import Referee.IShareableInfo;

/**
 * A strategy that attempts to place tiles in lexographic order. If no tiles
 * can be placed, exchanges if possible. If exchanging is not possible, passes.
 */
public abstract class AbstractStrategy implements IStrategy {
	@Override
	public IAction takeTurn(IShareableInfo gameInfo, List<ITile> hand) {
		List<Map.Entry<Coord, ITile>> placements = findAllPlacements(gameInfo, hand);
		if (placements.isEmpty()) {
			if (gameInfo.getRefTileCount() >= hand.size()) {
				return new ExchangeAction();
			} else {
				return new PassAction();
			}
		}
		return new PlaceAction(placements);
	}

	/**
	 * Finds all placements that can be made, in order of calling nextPlacement.
	 * @param gameInfo the current publicly available state of the game
	 * @param hand the hand of tiles to place
	 * @return the list of placements to make
	 */
	private List<Map.Entry<Coord, ITile>> findAllPlacements(IShareableInfo gameInfo, List<ITile> hand) {
		List<ITile> handCopy = new ArrayList<>(hand);
		List<Map.Entry<Coord, ITile>> placements = new ArrayList<>();
		IMap map = gameInfo.getMap();

		Optional<Map.Entry<Coord, ITile>> next =
				nextPlacement(map, hand, new ArrayList<>());
		while (next.isPresent()) {
			Map.Entry<Coord, ITile> placement = next.get();
			placements.add(placement);
			map.placeTileByOther(placement.getKey(), placement.getValue());
			handCopy.remove(placement.getValue());
			next = nextPlacement(map, handCopy, placements.stream().map(Map.Entry::getKey).collect(Collectors.toList()));
		}
		return placements;
	}

	protected boolean sameRowOrColumn(List<Coord> coords) {
		long distinctXsCount = coords.stream().map(Coord::getX).distinct().count();
		long distinctYsCount = coords.stream().map(Coord::getY).distinct().count();

		return distinctXsCount == 1 || distinctYsCount == 1;
	}

	/**
	 * Finds the next placement to make.
	 * @param map the map to place the tile on
	 * @param hand the hand of tiles to place
	 * @return the next placement to make, or empty if no placement can be made
	 */
	protected Optional<Map.Entry<Coord, ITile>> nextPlacement(
			IMap map, List<ITile> hand, List<Coord> placements
	) {
		for (ITile tile : hand.stream().sorted().collect(Collectors.toList())) {
			Optional<Coord> coord = findPlace(map, tile, placements);
			if (coord.isPresent()) {
				return Optional.of(new AbstractMap.SimpleEntry<>(coord.get(), tile));
			}
		}
		return Optional.empty();
	}

	/**
	 * Finds a place to put the tile on the board.
	 * @param map the map to place the tile on
	 * @param tile the tile to place
	 * @return the coordinate to place the tile at, or empty if no place is found
	 */
	protected abstract Optional<Coord> findPlace(
			IMap map, ITile tile, List<Coord> placements
	);
}
