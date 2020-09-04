package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class GenericResult {
  @NativeImplementation("genericResult")
  public static Obj genericResult(NativeApi nativeApi, Array array) {
    return nativeApi.factory().string("abc");
  }
}
