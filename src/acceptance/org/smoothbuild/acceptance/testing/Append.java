package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class Append {
  @NativeImplementation("testAppend")
  public static Array testAppend(NativeApi nativeApi, Array array, Obj element) {
    return nativeApi.factory()
        .arrayBuilder(array.spec().elemSpec())
        .addAll(array.asIterable(Obj.class))
        .add(element)
        .build();
  }
}
