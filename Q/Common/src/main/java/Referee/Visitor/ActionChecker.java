package Referee.Visitor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Action.ExchangeAction;
import Action.PassAction;
import Action.PlaceAction;
import Map.IMap;
import Map.Tile.ITile;
import Map.Coord;
import Referee.IGameState;

/**
 * An IActionChecker is able to check whether the proposed moves are valid with a visitor (referee)
 * on the current game state.
 */
public class ActionChecker implements IActionChecker {
	@Override
	public Boolean visitExchange(ExchangeAction action, IGameState gameState) {
		int playerTileCount = gameState.getActivePlayer().getTiles().size();
		return gameState.getRefTileCount() >= playerTileCount;
	}

	@Override
	public Boolean visitPass(PassAction action, IGameState gameState) {
		return true;
	}

	@Override
	public Boolean visitPlace(PlaceAction action, IGameState gameState) {
		if (action.getPlacements().size() == 0) {
			return false;
		}

		if (!sameRowOrColumn(
				action.getPlacements().stream().map(Map.Entry::getKey)
				.collect(Collectors.toList())
		)) {
			return false;
		}

		return placementsValid(
				action.getPlacements(),
				gameState.getMap(),
				gameState.getActivePlayer().getTiles()
		);
	}

	private boolean sameRowOrColumn(List<Coord> coords) {
		long distinctXsCount = coords.stream().map(Coord::getX).distinct().count();
		long distinctYsCount = coords.stream().map(Coord::getY).distinct().count();

		return distinctXsCount == 1 || distinctYsCount == 1;
	}

	private boolean placementsValid(List<Map.Entry<Coord, ITile>> placements, IMap map, List<ITile> playerTiles) {
		for (Map.Entry<Coord, ITile> placement : placements) {
			Coord coord = placement.getKey();
			ITile tile = placement.getValue();

			if (!playerTiles.remove(tile)) return false;
			if (!map.validSpots(tile).contains(coord)) return false;

			map.placeTileByOther(coord, tile);
		}

		return true;
	}
}
