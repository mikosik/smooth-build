package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.plugin.NativeApi;

public class BrokenIdentity {
  public static ValueH func(NativeApi nativeApi, ValueH value) {
    return nativeApi.factory().string("abc");
  }
}
