package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class ReturnNull {
  @SmoothFunction("returnNull")
  public static SString returnNull(NativeApi nativeApi) {
    return null;
  }
}
