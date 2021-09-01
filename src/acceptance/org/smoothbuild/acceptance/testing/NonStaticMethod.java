package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.plugin.NativeApi;

public class NonStaticMethod {
  public Str function(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
