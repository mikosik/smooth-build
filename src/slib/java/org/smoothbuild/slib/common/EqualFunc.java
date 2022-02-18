package org.smoothbuild.slib.common;

import org.smoothbuild.bytecode.obj.val.BoolB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class EqualFunc {
  public static BoolB func(NativeApi nativeApi, TupleB args) {
    ValB first = args.get(0);
    ValB second = args.get(1);
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
