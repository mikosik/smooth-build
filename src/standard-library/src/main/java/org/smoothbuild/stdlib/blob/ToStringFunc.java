package org.smoothbuild.stdlib.blob;

import static org.smoothbuild.common.Constants.CHARSET;

import java.io.IOException;
import okio.BufferedSource;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ToStringFunc {
  public static BValue func(NativeApi nativeApi, BTuple args)
      throws IOException, BytecodeException {
    BBlob blob = (BBlob) args.get(0);
    try (BufferedSource source = blob.source()) {
      return nativeApi.factory().string(source.readString(CHARSET));
    }
  }
}
