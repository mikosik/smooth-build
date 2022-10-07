package org.smoothbuild.slib.java;

import static org.smoothbuild.slib.compress.UnzipHelper.unzipToArrayB;

import java.io.IOException;

import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class UnjarFunc {
  public static final String JAR_MANIFEST_PATH = "META-INF/MANIFEST.MF";

  public static InstB func(NativeApi nativeApi, TupleB args) throws IOException {
    BlobB jar = (BlobB) args.get(0);
    return unzipToArrayB(nativeApi, jar, string -> !string.equals(JAR_MANIFEST_PATH));
  }
}
