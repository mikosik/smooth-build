package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Record;

public class BrokenIdentity {
  @SmoothFunction("brokenIdentity")
  public static Record brokenIdentity(NativeApi nativeApi, Record value) {
    return nativeApi.factory().string("abc");
  }
}
