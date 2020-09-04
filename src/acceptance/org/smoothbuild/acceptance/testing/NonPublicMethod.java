package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class NonPublicMethod {
  @NativeImplementation("function")
  static RString function(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
