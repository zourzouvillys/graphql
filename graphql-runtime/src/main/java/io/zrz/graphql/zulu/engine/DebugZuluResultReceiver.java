package io.zrz.graphql.zulu.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugZuluResultReceiver extends DefaultZuluResultReceiver implements ZuluResultReceiver {

  private static Logger log = LoggerFactory.getLogger(DebugZuluResultReceiver.class);

  @Override
  public void push(ZuluSelectionContainer container, Object instance) {
    log.debug("PUSH: {} [{}]", container, instance);
  }

  @Override
  public void pop(ZuluSelectionContainer container, Object instance) {
    log.debug("POP: {} [{}]", container, instance);
  }

  @Override
  public void next(Object instance) {
    log.debug("NEXT: {}", instance);
  }

  @Override
  public void write(ZuluSelection field, Object value) {
    log.debug("FIELD[{}]: {}", field.outputName(), value);
  }

}
