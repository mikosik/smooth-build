package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeImplementation;

public class WithoutContainer {
  @NativeImplementation("function")
  public static RString function() {
    return null;
  }
}
