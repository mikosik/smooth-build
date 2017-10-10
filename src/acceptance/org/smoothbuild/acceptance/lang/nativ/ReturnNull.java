package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class ReturnNull {
  @SmoothFunction
  public static SString returnNull(Container container) {
    return null;
  }
}
