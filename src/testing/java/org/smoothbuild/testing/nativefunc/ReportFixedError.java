package org.smoothbuild.testing.nativefunc;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ReportFixedError {
  public static ValB func(NativeApi nativeApi) {
    nativeApi.log().error("some error message");
    return null;
  }
}
