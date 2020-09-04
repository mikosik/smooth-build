package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ThrowException {
  @NativeImplementation("throwException")
  public static Obj throwException(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
