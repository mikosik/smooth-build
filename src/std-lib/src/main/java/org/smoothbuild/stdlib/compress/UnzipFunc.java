package org.smoothbuild.stdlib.compress;

import static org.smoothbuild.stdlib.compress.UnzipHelper.unzipToArrayB;

import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class UnzipFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    BlobB blob = (BlobB) args.get(0);
    return unzipToArrayB(nativeApi, blob, x -> true);
  }
}
