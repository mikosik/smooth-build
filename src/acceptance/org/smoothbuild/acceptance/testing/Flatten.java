package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class Flatten {
  @SmoothFunction("testFlatten")
  public static Array testFlatten(NativeApi nativeApi, Array array) {
    ConcreteType resultArrayElemType = ((ConcreteArrayType) array.type().elemType()).elemType();
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(resultArrayElemType);
    for (Array innerArray : array.asIterable(Array.class)) {
      builder.addAll(innerArray.asIterable(SObject.class));
    }
    return builder.build();
  }
}
