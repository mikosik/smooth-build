package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.RString;

public class ReturnNull {
  @SmoothFunction("returnNull")
  public static RString returnNull(NativeApi nativeApi) {
    return null;
  }
}
