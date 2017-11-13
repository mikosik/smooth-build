package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class ThrowRandomException {
  @SmoothFunction
  public static SString throwRandomException(Container container) {
    throw new UnsupportedOperationException(Long.toString(System.nanoTime()));
  }
}
