package org.smoothbuild.slib.bool;

import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class TrueFunction {
  @NativeImplementation("true")
  public static Bool trueFunction(NativeApi nativeApi) {
    return nativeApi.factory().bool(true);
  }
}
