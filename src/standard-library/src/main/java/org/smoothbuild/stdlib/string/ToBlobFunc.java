package org.smoothbuild.stdlib.string;

import static org.smoothbuild.common.Constants.CHARSET;

import java.io.IOException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ToBlobFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args)
      throws IOException, BytecodeException {
    var stringB = (StringB) args.get(0);
    var string = stringB.toJavaString();
    return nativeApi.factory().blob(sink -> sink.writeString(string, CHARSET));
  }
}
