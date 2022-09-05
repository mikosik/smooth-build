package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.StringB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ReportWarning {
  public static CnstB func(NativeApi nativeApi, TupleB args) {
    StringB message = (StringB) args.get(0);
    nativeApi.log().warning(message.toJ());
    return message;
  }
}
