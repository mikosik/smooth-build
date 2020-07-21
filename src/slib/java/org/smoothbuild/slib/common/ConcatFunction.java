package org.smoothbuild.slib.common;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.ArrayBuilder;
import org.smoothbuild.record.base.Record;

public class ConcatFunction {
  @SmoothFunction("concat")
  public static Array concat(NativeApi nativeApi, Array array1, Array array2) {
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(array1.spec().elemSpec());
    builder.addAll(array1.asIterable(Record.class));
    builder.addAll(array2.asIterable(Record.class));
    return builder.build();
  }
}
