package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.common.base.Strings;

public interface Named {
  public String name();

  public default String q() {
    return Strings.q(name());
  }
}
