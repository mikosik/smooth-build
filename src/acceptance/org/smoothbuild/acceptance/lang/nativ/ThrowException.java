package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class ThrowException {
  @SmoothFunction
  public static SString throwException(Container container) {
    throw new UnsupportedOperationException();
  }
}
