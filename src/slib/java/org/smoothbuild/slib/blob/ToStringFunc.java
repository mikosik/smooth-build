package org.smoothbuild.slib.blob;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.plugin.NativeApi;

import okio.BufferedSource;

public class ToStringFunc {
  public static StringB func(NativeApi nativeApi, TupleB args) throws IOException {
    BlobB blob = (BlobB) args.get(0);
    try (BufferedSource source = blob.source()) {
      return nativeApi.factory().string(source.readString(CHARSET));
    }
  }
}
