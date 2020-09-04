package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class BrokenIdentity {
  @NativeImplementation("brokenIdentity")
  public static Obj brokenIdentity(NativeApi nativeApi, Obj value) {
    return nativeApi.factory().string("abc");
  }
}
