package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.ArrayBBuilder;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.type.inst.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class Flatten {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    ArrayB array = (ArrayB) args.get(0);

    ArrayBBuilder builder = nativeApi.factory().arrayBuilder((ArrayTB) array.evalT().elem());
    for (ArrayB innerArray : array.elems(ArrayB.class)) {
      builder.addAll(innerArray.elems(InstB.class));
    }
    return builder.build();
  }
}
