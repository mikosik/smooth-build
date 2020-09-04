package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class Concat {
  @NativeImplementation("testConcat")
  public static Array testConcat(NativeApi nativeApi, Array first, Array second) {
    return nativeApi.factory()
        .arrayBuilder(first.spec().elemSpec())
        .addAll(first.asIterable(Obj.class))
        .addAll(second.asIterable(Obj.class))
        .build();
  }
}
