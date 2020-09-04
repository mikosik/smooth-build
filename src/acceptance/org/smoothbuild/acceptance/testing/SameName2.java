package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class SameName2 {
  @NativeImplementation("sameName")
  public static Str sameName(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
