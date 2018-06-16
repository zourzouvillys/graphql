package io.zrz.graphql.zulu.engine;

/**
 * results receiver which does nothing with the results.
 * 
 * all writes are forwarded to the object write (including the null write), so an implementation which doesn't care
 * about boxing/unboxing can override just that method.
 * 
 * @author theo
 *
 */

public abstract class DefaultZuluResultReceiver extends AbstractZuluResultReceiver implements ZuluResultReceiver {

  /**
   * this is the only method which implementations have to handle.
   */

  @Override
  public abstract void write(ZuluSelection field, Object value);

  @Override
  public void push(ZuluSelectionContainer container, Object instance) {
  }

  @Override
  public void pop(ZuluSelectionContainer container, Object instance) {
  }

  @Override
  public void next(Object instance) {
  }

  //

  @Override
  public void write(ZuluSelection field) {
    write(field, (Object) null);
  }

  @Override
  public void write(ZuluSelection field, int value) {
    write(field, (Object) value);
  }

  @Override
  public void write(ZuluSelection field, long value) {
    write(field, (Object) value);
  }

  @Override
  public void write(ZuluSelection field, boolean value) {
    write(field, (Object) value);
  }

  @Override
  public void write(ZuluSelection field, double value) {
    write(field, (Object) value);
  }

  @Override
  public void write(ZuluSelection field, String value) {
    write(field, (Object) value);
  }

}
