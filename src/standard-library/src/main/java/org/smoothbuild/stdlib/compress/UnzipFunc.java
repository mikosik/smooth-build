package org.smoothbuild.stdlib.compress;

import static org.smoothbuild.stdlib.compress.UnzipHelper.unzipToArrayB;

import java.io.IOException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class UnzipFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws IOException {
    BBlob blob = (BBlob) args.get(0);
    return unzipToArrayB(nativeApi, blob, x -> true);
  }
}
