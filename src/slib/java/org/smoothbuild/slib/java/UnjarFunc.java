package org.smoothbuild.slib.java;

import static org.smoothbuild.slib.compress.UnzipToArrayB.unzipToArrayB;

import java.io.IOException;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.plugin.NativeApi;

public class UnjarFunc {
  public static ArrayB func(NativeApi nativeApi, BlobB jar) throws IOException {
    return unzipToArrayB(nativeApi, jar, string -> !string.equals("META-INF/MANIFEST.MF"));
  }
}
