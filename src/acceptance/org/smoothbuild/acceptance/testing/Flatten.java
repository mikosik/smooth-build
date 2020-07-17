package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.ArrayBuilder;
import org.smoothbuild.record.base.SObject;
import org.smoothbuild.record.type.ArrayType;
import org.smoothbuild.record.type.BinaryType;

public class Flatten {
  @SmoothFunction("testFlatten")
  public static Array testFlatten(NativeApi nativeApi, Array array) {
    BinaryType resultArrayElemType = ((ArrayType) array.type().elemType()).elemType();
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(resultArrayElemType);
    for (Array innerArray : array.asIterable(Array.class)) {
      builder.addAll(innerArray.asIterable(SObject.class));
    }
    return builder.build();
  }
}
