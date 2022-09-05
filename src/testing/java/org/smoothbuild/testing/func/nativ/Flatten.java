package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.ArrayBBuilder;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class Flatten {
  public static CnstB func(NativeApi nativeApi, TupleB args) {
    ArrayB array = (ArrayB) args.get(0);

    ArrayBBuilder builder = nativeApi.factory().arrayBuilder((ArrayTB) array.cat().elem());
    for (ArrayB innerArray : array.elems(ArrayB.class)) {
      builder.addAll(innerArray.elems(CnstB.class));
    }
    return builder.build();
  }
}
