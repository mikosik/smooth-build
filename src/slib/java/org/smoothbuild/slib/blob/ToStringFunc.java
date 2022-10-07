package org.smoothbuild.slib.blob;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

import okio.BufferedSource;

public class ToStringFunc {
  public static InstB func(NativeApi nativeApi, TupleB args) throws IOException {
    BlobB blob = (BlobB) args.get(0);
    try (BufferedSource source = blob.source()) {
      return nativeApi.factory().string(source.readString(CHARSET));
    }
  }
}
