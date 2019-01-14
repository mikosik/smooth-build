package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Value;

public class Flatten {
  @SmoothFunction("testFlatten")
  public static Array testFlatten(NativeApi nativeApi, Array array) {
    ConcreteType resultArrayElemType = ((ConcreteArrayType) array.type().elemType()).elemType();
    ArrayBuilder builder = nativeApi.create().arrayBuilder(resultArrayElemType);
    for (Array innerArray : array.asIterable(Array.class)) {
      builder.addAll(innerArray.asIterable(Value.class));
    }
    return builder.build();
  }
}
