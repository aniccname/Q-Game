package Serialization.Deserializers;

import Serialization.JCell;
import Serialization.JTile;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class JCellDeserializer implements JsonDeserializer<JCell> {
  @Override
  public JCell deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

    Gson gson = new Gson();
    JsonArray jsonArray = jsonElement.getAsJsonArray();
    JsonElement jeColIndex = jsonArray.remove(0);
    int colIndex = jeColIndex.getAsInt();
    JTile jTile = gson.fromJson(jsonArray.get(0), JTile.class);

    return new JCell(colIndex, jTile);
  }
}
