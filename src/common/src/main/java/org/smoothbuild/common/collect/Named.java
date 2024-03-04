package org.smoothbuild.common.collect;

import org.smoothbuild.common.base.Strings;

public interface Named {
  public String name();

  public default String q() {
    return Strings.q(name());
  }
}
