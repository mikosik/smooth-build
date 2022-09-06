package org.smoothbuild.slib.array;

import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class ConcatFunc {
  public static ValB func(NativeApi nativeApi, TupleB args) {
    ArrayB array = (ArrayB) args.get(0);
    var factory = nativeApi.factory();
    var elemT = ((ArrayTB) array.type().elem()).elem();
    var resultBuilder = factory.arrayBuilderWithElems(elemT);
    var elems = array.elems(ArrayB.class);
    for (ArrayB elem : elems) {
      resultBuilder.addAll(elem.elems(ValB.class));
    }
    return resultBuilder.build();
  }
}
