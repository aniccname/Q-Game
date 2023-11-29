package Referee;

import java.util.List;
import java.util.stream.Collectors;

import Config.RefereeConfig;
import Player.IPlayer;

/**
 * A Mock referee for testing.
 */
public class MockReferee implements IReferee{
  private static GameResult everyoneWins(List<IPlayer> players) {
    return new GameResult(players.stream().map(IPlayer::name).collect(Collectors.toList()), List.of());
  }

  @Override
  public GameResult playGame(List<IPlayer> players, RefereeConfig refereeConfig) {
    return everyoneWins(players);
  }
}
