package org.smoothbuild.acceptance.testing;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class StringIdentity {
  public static StringB func(NativeApi nativeApi, StringB string) {
    return string;
  }
}
