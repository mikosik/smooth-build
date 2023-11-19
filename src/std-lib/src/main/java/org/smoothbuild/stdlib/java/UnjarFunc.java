package org.smoothbuild.stdlib.java;

import static org.smoothbuild.stdlib.compress.UnzipHelper.unzipToArrayB;

import java.io.IOException;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class UnjarFunc {
  public static final String JAR_MANIFEST_PATH = "META-INF/MANIFEST.MF";

  public static ValueB func(NativeApi nativeApi, TupleB args) throws IOException {
    BlobB jar = (BlobB) args.get(0);
    return unzipToArrayB(nativeApi, jar, string -> !string.equals(JAR_MANIFEST_PATH));
  }
}
