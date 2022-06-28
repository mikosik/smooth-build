package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static ArrayB func(NativeApi nativeApi, TupleB args) {
    ArrayB array = (ArrayB) args.get(0);
    CnstB elem = args.get(1);
    return nativeApi.factory()
        .arrayBuilderWithElems(array.cat().elem())
        .addAll(array.elems(CnstB.class))
        .add(elem)
        .build();
  }
}
