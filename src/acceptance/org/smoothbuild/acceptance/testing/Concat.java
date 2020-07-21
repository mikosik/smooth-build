package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.Record;

public class Concat {
  @SmoothFunction("testConcat")
  public static Array testConcat(NativeApi nativeApi, Array first, Array second) {
    return nativeApi.factory()
        .arrayBuilder(first.spec().elemSpec())
        .addAll(first.asIterable(Record.class))
        .addAll(second.asIterable(Record.class))
        .build();
  }
}
