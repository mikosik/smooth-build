package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Struct;

public class FileArrayToBlobArrayFunction {
  @SmoothFunction
  public static Array fileArrayToBlobArray(NativeApi nativeApi, Array files) {
    ArrayBuilder builder = nativeApi.create().arrayBuilder(nativeApi.types().blob());
    for (Struct file : files.asIterable(Struct.class)) {
      builder.add(file.get("content"));
    }
    return builder.build();
  }
}
