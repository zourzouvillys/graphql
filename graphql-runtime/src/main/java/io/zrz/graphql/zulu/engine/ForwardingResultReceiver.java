package io.zrz.graphql.zulu.engine;

import java.util.Objects;

/**
 * receiver which forwards all calls to another receiver unless overriden.
 * 
 * @author theo
 *
 */

public class ForwardingResultReceiver extends AbstractZuluResultReceiver implements ZuluResultReceiver {

  private ZuluResultReceiver target;

  public ForwardingResultReceiver(ZuluResultReceiver target) {
    this.target = Objects.requireNonNull(target);
  }

  @Override
  public void push(ZuluSelectionContainer container, Object instance) {
    target.push(container, instance);
  }

  @Override
  public void pop(ZuluSelectionContainer container, Object instance) {
    target.pop(container, instance);
  }

  @Override
  public void next(Object instance) {
    target.next(instance);
  }

  @Override
  public void write(ZuluSelection field) {
    target.write(field);
  }

  @Override
  public void write(ZuluSelection field, int value) {
    target.write(field, value);
  }

  @Override
  public void write(ZuluSelection field, long value) {
    target.write(field, value);
  }

  @Override
  public void write(ZuluSelection field, boolean value) {
    target.write(field, value);
  }

  @Override
  public void write(ZuluSelection field, double value) {
    target.write(field, value);
  }

  @Override
  public void write(ZuluSelection field, String value) {
    target.write(field, value);
  }

  @Override
  public void write(ZuluSelection field, Object value) {
    target.write(field, value);
  }

}
