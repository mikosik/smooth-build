package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class DifferentJavaName {
  @SmoothFunction("annotationName")
  public static SString methodName(NativeApi nativeApi) {
    return nativeApi.create().string("abc");
  }
}