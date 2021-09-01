package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.plugin.NativeApi;

public class ReturnNull {
  public static Str function(NativeApi nativeApi) {
    return null;
  }
}
