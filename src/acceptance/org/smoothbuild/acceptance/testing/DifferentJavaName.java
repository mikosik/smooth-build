package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class DifferentJavaName {
  @SmoothFunction("annotationName")
  public static SString methodName(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
