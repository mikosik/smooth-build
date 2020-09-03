package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class Append {
  @NativeImplementation("testAppend")
  public static Array testAppend(NativeApi nativeApi, Array array, Record element) {
    return nativeApi.factory()
        .arrayBuilder(array.spec().elemSpec())
        .addAll(array.asIterable(Record.class))
        .add(element)
        .build();
  }
}
