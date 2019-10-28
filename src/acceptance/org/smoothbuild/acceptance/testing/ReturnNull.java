package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ReturnNull {
  @SmoothFunction("returnNull")
  public static SString returnNull(NativeApi nativeApi) {
    return null;
  }
}
