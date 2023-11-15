package Referee;

import java.util.List;
import java.util.stream.Collectors;

import Player.IPlayer;

/**
 * A Mock referee for testing.
 */
public class MockReferee implements IReferee{
  @Override
  public GameResult playGame(List<IPlayer> players) {
    return this.everyoneWins(players);
  }

  @Override
  public GameResult playGame(List<IPlayer> players, IGameState gameState) {
    return this.everyoneWins(players);
  }

  private GameResult everyoneWins(List<IPlayer> players) {
    return new GameResult(players.stream().map(IPlayer::name).collect(Collectors.toList()), List.of());
  }
}
