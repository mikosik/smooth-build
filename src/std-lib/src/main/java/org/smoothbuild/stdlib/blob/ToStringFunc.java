package org.smoothbuild.stdlib.blob;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

import okio.BufferedSource;

public class ToStringFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws IOException {
    BlobB blob = (BlobB) args.get(0);
    try (BufferedSource source = blob.source()) {
      return nativeApi.factory().string(source.readString(CHARSET));
    }
  }
}
