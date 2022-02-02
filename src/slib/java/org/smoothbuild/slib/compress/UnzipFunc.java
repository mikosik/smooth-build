package org.smoothbuild.slib.compress;

import static org.smoothbuild.slib.compress.UnzipHelper.unzipToArrayB;

import java.io.IOException;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.plugin.NativeApi;

public class UnzipFunc {
  public static ArrayB func(NativeApi nativeApi, BlobB blob) throws IOException {
    return unzipToArrayB(nativeApi, blob, x -> true);
  }
}
