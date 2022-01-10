package org.smoothbuild.slib.array;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ElemFunc {
  public static ValB func(NativeApi nativeApi, ArrayB array, IntB index) {
    var elems = array.elems(ValB.class);
    int indexJ = index.toJ().intValue();
    if (indexJ < 0 || elems.size() <= indexJ) {
      nativeApi.log()
          .error("Index (" + indexJ + ") out of bounds. Array size = " + elems.size() + ".");
      return null;
    } else {
      return elems.get(indexJ);
    }
  }
}
