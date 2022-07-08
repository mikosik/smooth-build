package org.smoothbuild.util.collect;

import java.util.Optional;

import org.smoothbuild.util.Strings;

public interface Named extends Nameable {
  public String name();

  @Override
  public default Optional<String> nameO() {
    return Optional.of(name());
  }

  @Override
  public default String q() {
    return Strings.q(name());
  }
}
