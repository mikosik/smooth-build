package org.smoothbuild.slib.blob;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.bytecode.expr.value.BlobB;
import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.plugin.NativeApi;

import okio.BufferedSource;

public class ToStringFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws IOException {
    BlobB blob = (BlobB) args.get(0);
    try (BufferedSource source = blob.source()) {
      return nativeApi.factory().string(source.readString(CHARSET));
    }
  }
}
