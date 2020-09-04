package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeImplementation;

public class WithoutContainer {
  @NativeImplementation("function")
  public static Str function() {
    return null;
  }
}
