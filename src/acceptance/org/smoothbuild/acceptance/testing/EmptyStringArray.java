package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class EmptyStringArray {
  @NativeImplementation("emptyStringArray")
  public static Array emptyStringArray(NativeApi nativeApi) {
    return nativeApi.factory().arrayBuilder(nativeApi.factory().stringSpec()).build();
  }
}
