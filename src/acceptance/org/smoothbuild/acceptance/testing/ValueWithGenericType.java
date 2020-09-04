package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ValueWithGenericType {
  @NativeImplementation("valueWithGenericType")
  public static Obj valueWithGenericType(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
