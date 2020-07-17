package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class ReturnNull {
  @SmoothFunction("returnNull")
  public static SString returnNull(NativeApi nativeApi) {
    return null;
  }
}
