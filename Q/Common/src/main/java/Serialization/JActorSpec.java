package Serialization;

import java.util.List;
import java.util.Optional;

import Action.IAction;
import Map.IMap;
import Map.Tile.ITile;
import Player.IPlayer;
import Player.Player;
import Player.Strategy.DagStrategy;
import Player.Strategy.IStrategy;
import Player.Strategy.LdasgStrategy;
import Referee.IShareableInfo;

public class JActorSpec {
	public final String name;
	private final String jStrategy;
	private final String exn;

	public JActorSpec(String name, String strategy, String exn) {
		this.name = name;
		this.jStrategy = strategy;
		this.exn = exn;
	}

	public IPlayer convert() {
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

		return new Player(name, strategy) {
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
		};
	}
}
