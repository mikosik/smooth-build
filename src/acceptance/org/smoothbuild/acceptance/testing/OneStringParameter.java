package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class OneStringParameter {
  @NativeImplementation("oneStringParameter")
  public static Str oneStringParameter(NativeApi nativeApi, Str string) {
    return string;
  }
}
