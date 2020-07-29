package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class BrokenIdentity {
  @SmoothFunction("brokenIdentity")
  public static Record brokenIdentity(NativeApi nativeApi, Record value) {
    return nativeApi.factory().string("abc");
  }
}
