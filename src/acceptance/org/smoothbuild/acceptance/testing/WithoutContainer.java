package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.SmoothFunction;

public class WithoutContainer {
  @SmoothFunction("function")
  public static RString function() {
    return null;
  }
}
