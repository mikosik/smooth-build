package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.RString;

public class NonStaticMethod {
  @SmoothFunction("function")
  public RString function(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
