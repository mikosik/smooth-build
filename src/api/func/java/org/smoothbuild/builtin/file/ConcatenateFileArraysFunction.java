package org.smoothbuild.builtin.file;

import static org.smoothbuild.lang.type.Types.FILE;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SFile;

public class ConcatenateFileArraysFunction {
  @SmoothFunction
  public static Array concatenateFileArrays(NativeApi nativeApi, Array files, Array with) {
    ArrayBuilder builder = nativeApi.create().arrayBuilder(FILE);

    for (SFile file : files.asIterable(SFile.class)) {
      builder.add(file);
    }
    for (SFile file : with.asIterable(SFile.class)) {
      builder.add(file);
    }

    return builder.build();
  }
}
