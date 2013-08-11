package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.Type;

public class Arg {
  private final String name;
  private final Expression value;

  public Arg(String name, Expression value) {
    this.name = name;
    this.value = value;
  }

  public String name() {
    return name;
  }

  public Type<?> type() {
    return value.type();
  }

  public Expression value() {
    return value;
  }
}
