package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ReturnNull {
  @NativeImplementation("returnNull")
  public static Str returnNull(NativeApi nativeApi) {
    return null;
  }
}
