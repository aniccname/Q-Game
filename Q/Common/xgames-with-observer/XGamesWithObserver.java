import Config.RefereeConfig;
import Player.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.*;

import Observer.Observer;
import Observer.IObserver;
import Referee.GameResult;
import Referee.Referee;
import Serialization.Deserializers.JActorSpecDeserializer;
import Serialization.Deserializers.JRowDeserializer;
import Serialization.JRow;
import Serialization.JState;

public class XGamesWithObserver {
  public static void main(String[] args) {
    //TODO: Actually implement the spec. For now this is just for making sure that the observer works!
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(JRow.class, new JRowDeserializer())
            .registerTypeAdapter(Player.class, new JActorSpecDeserializer())
            .create();
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));

    JState jState = gson.fromJson(parser.next(), JState.class);
    Player[] players = gson.fromJson(parser.next(), Player[].class);

    RefereeConfig.RefereeConfigBuilder configBuilder =
        new RefereeConfig.RefereeConfigBuilder()
            .gameState(jState.convert());

    if (args.length > 0 && args[0].equals("-show")) {
      Observer obs = new Observer();
      obs.setVisible(true);
      obs.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      configBuilder.observer(obs);
    }

    GameResult result = new Referee().playGame(
            Arrays.stream(players).collect(Collectors.toList()),
            configBuilder.build()
    );

    System.out.println(gson.toJson(List.of(
            result.winners.stream().sorted().collect(Collectors.toList()),
            result.assholes
    )));
  }
}
