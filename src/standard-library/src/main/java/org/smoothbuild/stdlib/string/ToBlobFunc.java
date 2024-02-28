package org.smoothbuild.stdlib.string;

import static org.smoothbuild.common.Constants.CHARSET;

import java.io.IOException;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class ToBlobFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args)
      throws IOException, BytecodeException {
    var stringB = (StringB) args.get(0);
    var string = stringB.toJ();
    return nativeApi.factory().blob(sink -> sink.writeString(string, CHARSET));
  }
}
