package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class Flatten {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    ArrayB array = (ArrayB) args.get(0);

    ArrayBBuilder builder =
        nativeApi.factory().arrayBuilder((ArrayTB) array.evaluationT().elem());
    for (ArrayB innerArray : array.elems(ArrayB.class)) {
      builder.addAll(innerArray.elems(ValueB.class));
    }
    return builder.build();
  }
}
