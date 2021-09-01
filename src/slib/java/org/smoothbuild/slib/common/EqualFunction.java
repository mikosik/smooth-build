package org.smoothbuild.slib.common;

import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Val;
import org.smoothbuild.plugin.NativeApi;

public class EqualFunction {
  public static Bool function(NativeApi nativeApi, Val first, Val second) {
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
