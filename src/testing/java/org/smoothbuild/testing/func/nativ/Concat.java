package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class Concat {
  public static ArrayB func(NativeApi nativeApi, TupleB args) {
    ArrayB first = (ArrayB) args.get(0);
    ArrayB second = (ArrayB) args.get(1);

    var elemT = ((ArrayTB) nativeApi.typing().mergeUp(first.cat(), second.cat())).elem();
    return nativeApi.factory()
        .arrayBuilderWithElems(elemT)
        .addAll(first.elems(CnstB.class))
        .addAll(second.elems(CnstB.class))
        .build();
  }
}
