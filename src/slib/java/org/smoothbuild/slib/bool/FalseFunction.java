package org.smoothbuild.slib.bool;

import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class FalseFunction {
  @NativeImplementation("false")
  public static Bool falseFunction(NativeApi nativeApi) {
    return nativeApi.factory().bool(false);
  }
}
