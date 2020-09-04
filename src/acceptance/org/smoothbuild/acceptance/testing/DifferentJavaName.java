package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class DifferentJavaName {
  @NativeImplementation("annotationName")
  public static Str methodName(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
