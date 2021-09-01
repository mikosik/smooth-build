package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Val;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.ValSpec;
import org.smoothbuild.plugin.NativeApi;

public class Flatten {
  public static Array function(NativeApi nativeApi, Array array) {
    ValSpec resultArrayElemSpec = ((ArraySpec) array.spec().elemSpec()).elemSpec();
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(resultArrayElemSpec);
    for (Array innerArray : array.elements(Array.class)) {
      builder.addAll(innerArray.elements(Val.class));
    }
    return builder.build();
  }
}
