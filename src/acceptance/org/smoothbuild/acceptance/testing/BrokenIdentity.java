package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class BrokenIdentity {
  @SmoothFunction("brokenIdentity")
  public static SObject brokenIdentity(NativeApi nativeApi, SObject value) {
    return nativeApi.create().string("abc");
  }
}
