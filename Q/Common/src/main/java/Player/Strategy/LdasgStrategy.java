package Player.Strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import Map.Coord;
import Map.IMap;
import Map.Tile.ITile;

/**
 * Chooses the smallest (according to lexographic ordering) tile that can extend
 * the current map. Places the tile at the most-constrained location, breaking
 * ties using row-column ordering (smaller y values first, then smaller x
 * values). If no tiles can be placed, exchanges if possible. If exchanging
 * is not possible, passes.
 */
public class LdasgStrategy extends AbstractStrategy {
	@Override
	protected Optional<Coord> findPlace(IMap map, ITile tile, List<Coord> placements) {
		Set<Coord> possibleCoords = map.validSpots(tile);
		return possibleCoords.stream().filter(
				coord -> {
					List<Coord> coords = new ArrayList<>(placements);
					coords.add(coord);
					return sameRowOrColumn(coords);
				}
		).min(new MostConstrainedComparator(map));
	}

	/**
	 * Returns the number of neighbors of the given coordinate that are occupied
	 * by tiles.
	 * @param map the map to check neighbors on
	 * @param coord the coordinate to check neighbors of
	 * @return the number of neighbors of the given coordinate that are occupied
	 */
	private int occupiedNeighbors(IMap map, Coord coord) {
		return (int) Arrays.stream(coord.getCardinalNeighbors())
				.filter(neighbor -> !map.getTile(neighbor).isEmpty())
				.count();
	}

	/**
	 * Compares coordinates by the number of neighbors that are occupied
	 * by tiles. Breaks ties using row-column ordering (smaller y values
	 * first, then smaller x values).
	 */
	private class MostConstrainedComparator implements Comparator<Coord> {
		private final IMap map;

		private MostConstrainedComparator(IMap map) {
			this.map = map;
		}

		@Override
		public int compare(Coord o1, Coord o2) {
			int o1Neighbors = occupiedNeighbors(map, o1);
			int o2Neighbors = occupiedNeighbors(map, o2);
			if (o1Neighbors != o2Neighbors) {
				return o2Neighbors - o1Neighbors;
			} else {
				return o1.compareTo(o2);
			}
		}
	}
}
