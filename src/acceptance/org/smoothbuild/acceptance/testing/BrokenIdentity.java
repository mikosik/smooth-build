package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.SObject;

public class BrokenIdentity {
  @SmoothFunction("brokenIdentity")
  public static SObject brokenIdentity(NativeApi nativeApi, SObject value) {
    return nativeApi.factory().string("abc");
  }
}
