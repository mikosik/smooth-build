package org.smoothbuild.stdlib.array;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

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
