package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Record;

public class BrokenIdentity {
  @SmoothFunction("brokenIdentity")
  public static Record brokenIdentity(NativeApi nativeApi, Record value) {
    return nativeApi.factory().string("abc");
  }
}
