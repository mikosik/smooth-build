package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.Record;

public class Append {
  @SmoothFunction("testAppend")
  public static Array testAppend(NativeApi nativeApi, Array array, Record element) {
    return nativeApi.factory()
        .arrayBuilder(array.spec().elemSpec())
        .addAll(array.asIterable(Record.class))
        .add(element)
        .build();
  }
}
