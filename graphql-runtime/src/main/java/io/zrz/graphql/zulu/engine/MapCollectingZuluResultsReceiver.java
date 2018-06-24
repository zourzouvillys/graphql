package io.zrz.graphql.zulu.engine;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapCollectingZuluResultsReceiver extends DefaultZuluResultReceiver implements ZuluResultReceiver {

  private final LinkedHashMap<ZuluSelection, Object> results = new LinkedHashMap<>();

  public Map<ZuluSelection, Object> result() {
    return results;
  }

  @Override
  public void write(ZuluSelection field, Object value) {
    results.put(field, value);
  }

  @Override
  public void startList(ZuluSelection container, Object context) {
  }

  @Override
  public void endList(ZuluSelection container, Object context) {
  }

  @Override
  public void startStruct(ZuluSelectionContainer container, Object context) {
    // TODO Auto-generated method stub

  }

  @Override
  public void endStruct(ZuluSelectionContainer container, Object context) {
    // TODO Auto-generated method stub

  }

}
