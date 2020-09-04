package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class Flatten {
  @NativeImplementation("testFlatten")
  public static Array testFlatten(NativeApi nativeApi, Array array) {
    Spec resultArrayElemSpec = ((ArraySpec) array.spec().elemSpec()).elemSpec();
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(resultArrayElemSpec);
    for (Array innerArray : array.asIterable(Array.class)) {
      builder.addAll(innerArray.asIterable(Obj.class));
    }
    return builder.build();
  }
}
