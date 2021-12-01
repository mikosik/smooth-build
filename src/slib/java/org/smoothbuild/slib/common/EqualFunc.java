package org.smoothbuild.slib.common;

import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.plugin.NativeApi;

public class EqualFunc {
  public static BoolH func(NativeApi nativeApi, ValH first, ValH second) {
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
