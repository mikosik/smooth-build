package org.smoothbuild.util.collect;

import java.util.Optional;

public interface Nameable {
  public Optional<String> nameO();

  public default String nameSane() {
    return nameO().orElse("");
  }

  public default String q() {
    return "`" + nameSane() + "`";
  }
}
