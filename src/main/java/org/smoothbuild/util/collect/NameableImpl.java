package org.smoothbuild.util.collect;

import java.util.Optional;

public class NameableImpl implements Nameable {
  private final Optional<String> name;

  public NameableImpl(String name) {
    this(Optional.of(name));
  }
  public NameableImpl(Optional<String> name) {
    this.name = name;
  }

  @Override
  public Optional<String> nameO() {
    return name;
  }

  @Override
  public String toString() {
    return nameSane();
  }
}
