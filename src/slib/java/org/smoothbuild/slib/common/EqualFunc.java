package org.smoothbuild.slib.common;

import org.smoothbuild.bytecode.obj.val.BoolB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class EqualFunc {
  public static BoolB func(NativeApi nativeApi, ValB first, ValB second) {
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
