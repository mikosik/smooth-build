package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Struct;

public class ConcatenateFileArraysFunction {
  @SmoothFunction
  public static Array concatenateFileArrays(NativeApi nativeApi, Array files, Array with) {
    ArrayBuilder builder = nativeApi.create().arrayBuilder(nativeApi.types().file());
    builder.addAll(files.asIterable(Struct.class));
    builder.addAll(with.asIterable(Struct.class));
    return builder.build();
  }
}
