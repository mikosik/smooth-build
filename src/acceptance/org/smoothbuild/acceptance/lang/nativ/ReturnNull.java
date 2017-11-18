package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class ReturnNull {
  @SmoothFunction
  public static SString returnNull(NativeApi nativeApi) {
    return null;
  }
}
