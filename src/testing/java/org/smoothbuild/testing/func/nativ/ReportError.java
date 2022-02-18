package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ReportError {
  public static ValB func(NativeApi nativeApi, TupleB args) {
    StringB message = (StringB) args.get(0);

    nativeApi.log().error(message.toJ());
    return null;
  }
}
