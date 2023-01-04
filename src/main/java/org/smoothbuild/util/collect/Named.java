package org.smoothbuild.util.collect;

import org.smoothbuild.util.Strings;

public interface Named {
  public String name();

  public default String q() {
    return Strings.q(name());
  }
}
