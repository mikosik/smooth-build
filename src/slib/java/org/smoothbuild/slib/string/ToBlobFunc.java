package org.smoothbuild.slib.string;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ToBlobFunc {
  public static ValB func(NativeApi nativeApi, TupleB args) throws IOException {
    StringB string = (StringB) args.get(0);
    return nativeApi.factory().blob(sink -> sink.writeString(string.toJ(), CHARSET));
  }
}
