package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.RString;

public class WithoutContainer {
  @SmoothFunction("function")
  public static RString function() {
    return null;
  }
}
