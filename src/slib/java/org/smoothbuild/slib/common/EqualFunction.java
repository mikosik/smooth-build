package org.smoothbuild.slib.common;

import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;

public class EqualFunction {
  public static Bool function(NativeApi nativeApi, Obj first, Obj second) {
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
