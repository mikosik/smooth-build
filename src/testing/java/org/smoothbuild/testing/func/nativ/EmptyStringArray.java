package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class EmptyStringArray {
  public static ArrayB func(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().arrayBuilderWithElems(nativeApi.factory().stringT()).build();
  }
}
