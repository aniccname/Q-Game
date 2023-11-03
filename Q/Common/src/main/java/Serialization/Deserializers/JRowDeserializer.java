package Serialization.Deserializers;

import Serialization.JCell;
import Serialization.JRow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class JRowDeserializer implements JsonDeserializer<JRow> {
  @Override
  public JRow deserialize(
          JsonElement jsonElement,
          Type type,
          JsonDeserializationContext jsonDeserializationContext
  ) throws JsonParseException {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(JCell.class, new JCellDeserializer())
            .create();
    JsonArray jsonArray = jsonElement.getAsJsonArray();
    JsonElement jeRowIndex = jsonArray.remove(0);
    int rowIndex = jeRowIndex.getAsInt();
    JCell[] jCells = gson.fromJson(jsonArray, JCell[].class);

    return new JRow(rowIndex, jCells);
  }
}
