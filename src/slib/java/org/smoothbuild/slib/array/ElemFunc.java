package org.smoothbuild.slib.array;

import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ElemFunc {
  public static ValB func(NativeApi nativeApi, TupleB args) {
    ArrayB array = (ArrayB) args.get(0);
    IntB index = (IntB) args.get(1);
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
