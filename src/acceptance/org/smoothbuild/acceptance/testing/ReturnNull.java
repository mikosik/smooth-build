package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class ReturnNull {
  @SmoothFunction("returnNull")
  public static RString returnNull(NativeApi nativeApi) {
    return null;
  }
}
