package Referee.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Action.ExchangeAction;
import Action.PassAction;
import Action.PlaceAction;
import Config.ScoringConfig;
import Map.Coord;
import Map.IMap;
import Map.Tile.ITile;

/**
 * An ActionScorer is able to give actions a score based on what action was taken.
 */
public class ActionScorer implements IVisitor<IMap, Integer> {
	private final ScoringConfig scoringConfig;
	private final boolean placedEntireHand;

	/**
	 * @param placedEntireHand whether the player placed their entire hand
	 * @param scoringConfig the scoring configuration to use
	 */
	public ActionScorer(boolean placedEntireHand, ScoringConfig scoringConfig) {
		this.placedEntireHand = placedEntireHand;
		this.scoringConfig = scoringConfig;
	}

	/**
	 * An exchange action does not produce any score hence return 0
	 *
	 * @param map the map of the game to score
	 * @param action the exchange action visited
	 * @return the score of 0 as the exchange does not have any score
	 */
	public Integer visitExchange(ExchangeAction action, IMap map) {
		return 0;
	}

	/**
	 * A pass action does not produce any score hence return 0
	 *
	 * @param map the map of the game to score
	 * @param action the pass action visited
	 * @return an integer representing the score of the pass action = 0
	 */
	public Integer visitPass(PassAction action, IMap map) {
		return 0;
	}

	/**
	 * Computes the score of the place action based on the following
	 * - a sequence that the action adds to
	 * - whether a Q was completed
	 * - whether all the tiles in the players hand were placed
	 *
	 * @param map the map of the game to score
	 * @param action the place action visited
	 * @return the score represented what the active player should receive for placing all the tiles
	 */
	public Integer visitPlace(PlaceAction action, IMap map) {
		return pointsFromTilesPlaced(action)
				+ pointsFromSequences(map, action)
				+ pointsFromQ(map, action)
				+ pointsFromPlacingEntireHand(action);
	}

	/**
	 * Awards points for each tile placed
	 */
	private int pointsFromTilesPlaced(PlaceAction action) {
		return action.getPlacements().size();
	}

	/**
	 * Award points for the length of each sequence containing a placed tile
	 */
	private int pointsFromSequences(IMap map, PlaceAction action) {
		// updates the score based on sequence length
		return sequences(map, action).stream().mapToInt(Sequence::getLength).sum();
	}

	/**
	 * Awards points for each Q that contains a placed tile
	 */
	private int pointsFromQ(IMap map, PlaceAction action) {
		return (int) sequences(map, action).stream()
				.filter(sequence -> sequence.isQ(map)).count() * scoringConfig.qPoints();
	}

	/**
	 * Awards a bonus for placing the player's entire hand
	 */
	private int pointsFromPlacingEntireHand(PlaceAction action) {
		if (placedEntireHand) {
			return scoringConfig.wholeHandBonus();
		} else {
			return 0;
		}
	}

	/**
	 * obtains all the distinct sequences of size greater than 1 that are
	 * on the board that will be made from the placement
	 */
	private List<Sequence> sequences(IMap map, PlaceAction action) {
		List<Coord> coords = action.getPlacements().stream()
				.map(Map.Entry::getKey).toList();

		return coords.stream()
				.flatMap(coord -> getSequences(map, coord).stream())
				.filter(sequence -> sequence.getLength() > 1)
				.distinct().collect(Collectors.toList());
	}

	/**
	 * Computes all the sequences from placing a specified coord onto the board.
	 *
	 * @param map the map to use to compute the sequences with
	 * @param coord the specified coord to score
	 * @return a list of sequences that should be scored.
	 */
	private List<Sequence> getSequences(IMap map, Coord coord) {
		int minX = coord.getX(); // take a guess
		// find true minimum coord x,y
		while (!map.getTile(new Coord(minX-1, coord.getY())).isEmpty()) {
			minX--;
		}
		int maxX = coord.getX();
		while (!map.getTile(new Coord(maxX+1, coord.getY())).isEmpty()) {
			maxX++;
		}
		int minY = coord.getY();
		while (!map.getTile(new Coord(coord.getX(), minY-1)).isEmpty()) {
			minY--;
		}
		int maxY = coord.getY();
		while (!map.getTile(new Coord(coord.getX(), maxY+1)).isEmpty()) {
			maxY++;
		}
		// find the horizontal/vertical lines within the q game
		return List.of(new Sequence(new Coord(minX, coord.getY()), new Coord(maxX, coord.getY())),
				new Sequence(new Coord(coord.getX(), minY), new Coord(coord.getX(), maxY)));
	}

	/**
	 * A sequence represents a contiguous sequence of tiles on the game map that should
	 * be included in the score.
	 * - a sequence can be a horizontal line to look at
	 * - a sequence can be a vertical line to look at
	 */
	static class Sequence {
		private final Coord start; // the starting coord of the sequence
		private final Coord end; // the ending coord of the sequence

		Sequence(Coord start, Coord end) {
			this.start = start;
			this.end = end;
		}

		/**
		 * Returns the length of the sequence.
		 *
		 * @return the int representing length of the sequence.
		 */
		public int getLength() {
			return 1 + Math.abs(start.getX() - end.getX()) + Math.abs(start.getY() - end.getY());
		}

		/**
		 * Determines whether a sequence is a Q
		 *
		 * @param map the map to use to determine whether it is a q
		 * @return a boolean representing this result
		 */
		public boolean isQ(IMap map) {
			if (getLength() != 6) return false;
			List<ITile> tiles = getAllCoords().stream().map(map::getTile).toList();
			long numShapes = tiles.stream().map(ITile::getShape).distinct().count();
			long numColors = tiles.stream().map(ITile::getColor).distinct().count();
			return numShapes == 6 || numColors == 6;
		}

		/**
		 * Gets all the coordinates in the sequence.
		 *
		 * @return a list of coordinates representing all the coordinates in a scoring sequence.
		 */
		private List<Coord> getAllCoords() {
			List<Coord> coords = new ArrayList<>();
			if (start.getX() == end.getX()) {
				for (int y = start.getY(); y <= end.getY(); y++) {
					coords.add(new Coord(start.getX(), y));
				}
			} else {
				for (int x = start.getX(); x <= end.getX(); x++) {
					coords.add(new Coord(x, start.getY()));
				}
			}
			return coords;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Sequence)) return false;
			Sequence other = (Sequence) obj;
			return (start.equals(other.start) && end.equals(other.end))
					|| (start.equals(other.end) && end.equals(other.start));
		}

		@Override
		public int hashCode() {
			return start.hashCode() + end.hashCode();
		}
	}
}
