package org.smoothbuild.util.collect;

import java.util.Optional;

public interface Named extends Nameable {
  public default String name() {
    return nameO().get();
  }

  @Override
  public default Optional<String> nameO() {
    return Optional.of(name());
  }

  @Override
  public default String q() {
    return "`" + name() + "`";
  }
}
