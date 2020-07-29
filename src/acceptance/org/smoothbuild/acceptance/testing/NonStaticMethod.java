package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class NonStaticMethod {
  @SmoothFunction("function")
  public RString function(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
