package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.plugin.NativeApi;

public class WrongMethodName {
  public static StringH wrongMethodName(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
