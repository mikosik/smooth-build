package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.value.ArrayB;
import org.smoothbuild.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.bytecode.type.value.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class Flatten {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    ArrayB array = (ArrayB) args.get(0);

    ArrayBBuilder builder = nativeApi.factory().arrayBuilder((ArrayTB) array.evalT().elem());
    for (ArrayB innerArray : array.elems(ArrayB.class)) {
      builder.addAll(innerArray.elems(ValueB.class));
    }
    return builder.build();
  }
}
