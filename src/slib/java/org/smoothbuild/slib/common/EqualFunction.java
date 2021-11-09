package org.smoothbuild.slib.common;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.plugin.NativeApi;

public class EqualFunction {
  public static BoolH function(NativeApi nativeApi, ValueH first, ValueH second) {
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
