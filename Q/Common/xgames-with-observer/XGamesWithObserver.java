import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;

import Observer.Observer;
import Referee.GameResult;
import Referee.Referee;
import Serialization.Deserializers.JActorSpecDeserializer;
import Serialization.Deserializers.JRowDeserializer;
import Serialization.JActorSpec;
import Serialization.JRow;
import Serialization.JState;

public class XGamesWithObserver {
  public static void main(String[] args) {
    //TODO: Actually implement the spec. For now this is just for making sure that the observer works!
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(JRow.class, new JRowDeserializer())
            .registerTypeAdapter(JActorSpec.class, new JActorSpecDeserializer())
            .create();
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));

    JState jState = gson.fromJson(parser.next(), JState.class);
    JActorSpec[] jActors = gson.fromJson(parser.next(), JActorSpec[].class);

    Observer obs = new Observer();
    obs.setVisible(true);
    obs.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    GameResult result = new Referee().playGame(
            Arrays.stream(jActors).map(JActorSpec::convert).collect(Collectors.toList()),
            jState.convert(Arrays.stream(jActors).map(actor -> actor.name).collect(Collectors.toList())),
            List.of(obs)
    );

    System.out.println(gson.toJson(List.of(
            result.winners.stream().sorted().collect(Collectors.toList()),
            result.assholes
    )));
  }
}
