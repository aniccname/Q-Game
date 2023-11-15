package Serialization.Deserializers;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

import Action.ExchangeAction;
import Action.IAction;
import Action.PassAction;
import Action.PlaceAction;
import Serialization.OnePlacement;

public class JChoiceDeserializer implements JsonDeserializer<IAction> {

  @Override
  public IAction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    if (jsonElement.isJsonPrimitive() && ((JsonPrimitive) jsonElement).isString()) {
      if (jsonElement.getAsString().equals("pass")) {
        return new PassAction();
      }
      else if (jsonElement.getAsString().equals("replace")) {
        return new ExchangeAction();
      }
    }
    else if (jsonElement.isJsonArray()) {
      var tiles = Arrays.stream(new Gson().fromJson(jsonElement, OnePlacement[].class))
              .map(OnePlacement::convert).collect(Collectors.toList());
      return new PlaceAction(tiles);
    }
    throw new IllegalArgumentException("Invalid JChoice " + jsonElement);
  }
}
