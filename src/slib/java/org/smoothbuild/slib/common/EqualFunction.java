package org.smoothbuild.slib.common;

import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class EqualFunction {
  @NativeImplementation("equal")
  public static Bool equalFunction(NativeApi nativeApi, Obj first, Obj second) {
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
