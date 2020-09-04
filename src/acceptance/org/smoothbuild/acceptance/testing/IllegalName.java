package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class IllegalName {
  @NativeImplementation("illegalName$")
  public static Str illegalName$(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
