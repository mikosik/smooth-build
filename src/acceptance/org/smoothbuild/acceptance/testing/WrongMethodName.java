package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.plugin.NativeApi;

public class WrongMethodName {
  public static Str wrongMethodName(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
