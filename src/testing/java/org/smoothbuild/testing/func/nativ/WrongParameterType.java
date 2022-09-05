package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.plugin.NativeApi;

public class WrongParameterType {
  public static CnstB func(NativeApi nativeApi, NativeApi nativeApi2) {
    return null;
  }
}
