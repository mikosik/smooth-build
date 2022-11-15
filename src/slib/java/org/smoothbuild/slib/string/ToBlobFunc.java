package org.smoothbuild.slib.string;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.bytecode.expr.inst.StringB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class ToBlobFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws IOException {
    StringB string = (StringB) args.get(0);
    return nativeApi.factory().blob(sink -> sink.writeString(string.toJ(), CHARSET));
  }
}
