package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Value;

public class BrokenIdentity {
  @SmoothFunction("brokenIdentity")
  public static Value brokenIdentity(NativeApi nativeApi, Value value) {
    return nativeApi.create().string("abc");
  }
}
