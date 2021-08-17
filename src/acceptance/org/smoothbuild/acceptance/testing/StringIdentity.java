package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;

public class StringIdentity {
  public static Str function(NativeApi nativeApi, Str string) {
    return string;
  }
}
