package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.ArrayBuilder;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.spec.ArraySpec;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class Flatten {
  @SmoothFunction("testFlatten")
  public static Array testFlatten(NativeApi nativeApi, Array array) {
    Spec resultArrayElemSpec = ((ArraySpec) array.spec().elemSpec()).elemSpec();
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(resultArrayElemSpec);
    for (Array innerArray : array.asIterable(Array.class)) {
      builder.addAll(innerArray.asIterable(Record.class));
    }
    return builder.build();
  }
}
