package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class NonStaticMethod {
  @NativeImplementation("function")
  public RString function(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
