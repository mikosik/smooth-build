package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class WithoutContainer {
  @SmoothFunction("function")
  public static SString function() {
    return null;
  }
}
