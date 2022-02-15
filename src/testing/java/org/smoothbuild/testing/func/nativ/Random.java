package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class Random {
  public static StringB func(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(new java.util.Random().nextInt()));
  }
}
