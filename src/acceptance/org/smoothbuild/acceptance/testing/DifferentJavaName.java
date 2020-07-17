package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class DifferentJavaName {
  @SmoothFunction("annotationName")
  public static SString methodName(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
