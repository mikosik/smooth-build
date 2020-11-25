package org.smoothbuild.cli.console;

public class Maybe<V> extends MemoryLogger {
  private V value;

  public Maybe() {
    this.value = null;
  }

  public <T> Maybe(MemoryLogger logger) {
    super(logger);
    this.value = null;
  }

  public void setValue(V value) {
    this.value = value;
  }

  public V value() {
    return value;
  }
}
