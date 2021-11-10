package org.smoothbuild.util.collect;

import java.util.Optional;

public interface Named extends Nameable {
  public String name();

  @Override
  public default Optional<String> nameO() {
    return Optional.of(name());
  }
}
