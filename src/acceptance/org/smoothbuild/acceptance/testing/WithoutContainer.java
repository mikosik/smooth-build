package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class WithoutContainer {
  @SmoothFunction("function")
  public static SString function() {
    return null;
  }
}
