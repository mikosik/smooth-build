package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class OneStringParameter {
  @NativeImplementation("oneStringParameter")
  public static RString oneStringParameter(NativeApi nativeApi, RString string) {
    return string;
  }
}
