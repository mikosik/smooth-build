package org.smoothbuild.stdlib.compress;

import static org.smoothbuild.stdlib.compress.UnzipHelper.unzipToArrayB;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class UnzipFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    BlobB blob = (BlobB) args.get(0);
    return unzipToArrayB(nativeApi, blob, x -> true);
  }
}
