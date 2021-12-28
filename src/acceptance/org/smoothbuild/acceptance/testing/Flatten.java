package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.bytecode.obj.val.ArrayBBuilder;
import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.db.bytecode.type.val.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class Flatten {
  public static ArrayB func(NativeApi nativeApi, ArrayB array) {
    ArrayBBuilder builder = nativeApi.factory().arrayBuilder((ArrayTB) array.cat().elem());
    for (ArrayB innerArray : array.elems(ArrayB.class)) {
      builder.addAll(innerArray.elems(ValB.class));
    }
    return builder.build();
  }
}
