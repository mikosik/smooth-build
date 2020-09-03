package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class BrokenIdentity {
  @NativeImplementation("brokenIdentity")
  public static Record brokenIdentity(NativeApi nativeApi, Record value) {
    return nativeApi.factory().string("abc");
  }
}
