package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class NonPublicMethod {
  static StringB func(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
