package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.plugin.NativeApi;

public class Flatten {
  public static Array function(NativeApi nativeApi, Array array) {
    ValSpec resultArrayElemSpec = ((ArraySpec) array.spec().element()).element();
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(resultArrayElemSpec);
    for (Array innerArray : array.elements(Array.class)) {
      builder.addAll(innerArray.elements(Val.class));
    }
    return builder.build();
  }
}
