package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.plugin.NativeApi;

public class BrokenIdentity {
  public static Val function(NativeApi nativeApi, Val value) {
    return nativeApi.factory().string("abc");
  }
}
