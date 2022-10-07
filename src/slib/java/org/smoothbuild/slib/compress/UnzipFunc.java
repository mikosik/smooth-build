package org.smoothbuild.slib.compress;

import static org.smoothbuild.slib.compress.UnzipHelper.unzipToArrayB;

import java.io.IOException;

import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class UnzipFunc {
  public static InstB func(NativeApi nativeApi, TupleB args) throws IOException {
    BlobB blob = (BlobB) args.get(0);
    return unzipToArrayB(nativeApi, blob, x -> true);
  }
}
