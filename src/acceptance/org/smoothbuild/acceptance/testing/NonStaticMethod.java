package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class NonStaticMethod {
  public StringB func(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
