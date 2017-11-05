package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SFile;

public class ConcatenateFileArraysFunction {
  @SmoothFunction
  public static Array<SFile> concatenateFileArrays(Container container, Array<SFile> files,
      Array<SFile> with) {
    ArrayBuilder<SFile> builder = container.create().arrayBuilder(SFile.class);

    for (SFile file : files) {
      builder.add(file);
    }
    for (SFile file : with) {
      builder.add(file);
    }

    return builder.build();
  }
}
