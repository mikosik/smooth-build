package org.smoothbuild.stdlib.blob;

import static okio.Okio.buffer;
import static org.smoothbuild.common.Constants.CHARSET;

import java.io.IOException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ToStringFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws IOException {
    BBlob blob = (BBlob) args.get(0);
    try (var source = buffer(blob.source())) {
      return nativeApi.factory().string(source.readString(CHARSET));
    }
  }
}
