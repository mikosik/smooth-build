package org.smoothbuild.stdlib.array;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class ConcatFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    ArrayB array = (ArrayB) args.get(0);
    var factory = nativeApi.factory();
    var elemT = ((ArrayTB) array.evaluationT().elem()).elem();
    var resultBuilder = factory.arrayBuilderWithElems(elemT);
    var elems = array.elems(ArrayB.class);
    for (ArrayB elem : elems) {
      resultBuilder.addAll(elem.elems(ValueB.class));
    }
    return resultBuilder.build();
  }
}
