package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class DifferentJavaName {
  @SmoothFunction("annotationName")
  public static RString methodName(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
