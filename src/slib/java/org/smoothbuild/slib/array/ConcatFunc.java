package org.smoothbuild.slib.array;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.bytecode.type.inst.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class ConcatFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    ArrayB array = (ArrayB) args.get(0);
    var factory = nativeApi.factory();
    var elemT = ((ArrayTB) array.evalT().elem()).elem();
    var resultBuilder = factory.arrayBuilderWithElems(elemT);
    var elems = array.elems(ArrayB.class);
    for (ArrayB elem : elems) {
      resultBuilder.addAll(elem.elems(ValueB.class));
    }
    return resultBuilder.build();
  }
}
