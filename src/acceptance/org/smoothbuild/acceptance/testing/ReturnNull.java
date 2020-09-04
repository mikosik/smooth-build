package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ReturnNull {
  @NativeImplementation("returnNull")
  public static RString returnNull(NativeApi nativeApi) {
    return null;
  }
}
