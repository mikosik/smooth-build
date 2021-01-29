package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;

public class BrokenIdentity {
  public static Obj function(NativeApi nativeApi, Obj value) {
    return nativeApi.factory().string("abc");
  }
}
