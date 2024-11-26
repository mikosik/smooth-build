package org.smoothbuild.stdlib.java;

import static org.smoothbuild.stdlib.compress.UnzipHelper.unzipToArrayB;

import java.io.IOException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class UnjarFunc {
  public static final String JAR_MANIFEST_PATH = "META-INF/MANIFEST.MF";

  public static BValue func(NativeApi nativeApi, BTuple args) throws IOException {
    BBlob jar = (BBlob) args.get(0);
    return unzipToArrayB(nativeApi, jar, string -> !string.equals(JAR_MANIFEST_PATH));
  }
}
