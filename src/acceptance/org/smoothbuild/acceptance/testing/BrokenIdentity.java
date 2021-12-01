package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.plugin.NativeApi;

public class BrokenIdentity {
  public static ValH func(NativeApi nativeApi, ValH val) {
    return nativeApi.factory().string("abc");
  }
}
