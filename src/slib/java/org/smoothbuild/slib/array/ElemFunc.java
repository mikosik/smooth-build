package org.smoothbuild.slib.array;

import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.IntB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ElemFunc {
  public static CnstB func(NativeApi nativeApi, TupleB args) {
    ArrayB array = (ArrayB) args.get(0);
    IntB index = (IntB) args.get(1);
    var elems = array.elems(CnstB.class);
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
