package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class Flatten {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    ArrayB array = (ArrayB) args.get(0);

    ArrayBBuilder builder =
        nativeApi.factory().arrayBuilder((ArrayTB) array.evaluationType().elem());
    for (ArrayB innerArray : array.elements(ArrayB.class)) {
      builder.addAll(innerArray.elements(ValueB.class));
    }
    return builder.build();
  }
}
