package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static ArrayB func(NativeApi nativeApi, TupleB args) {
    ArrayB array = (ArrayB) args.get(0);
    ValB elem = args.get(1);
    return nativeApi.factory()
        .arrayBuilderWithElems(nativeApi.typing().mergeUp(array.cat().elem(), elem.cat()))
        .addAll(array.elems(ValB.class))
        .add(elem)
        .build();
  }
}