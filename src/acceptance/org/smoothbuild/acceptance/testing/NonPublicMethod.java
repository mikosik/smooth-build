package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.plugin.NativeApi;

public class NonPublicMethod {
  static StringH func(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
