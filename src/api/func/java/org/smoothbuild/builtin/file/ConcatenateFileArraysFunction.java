package org.smoothbuild.builtin.file;

import static org.smoothbuild.lang.type.Types.FILE;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Value;

public class ConcatenateFileArraysFunction {
  @SmoothFunction
  public static Array concatenateFileArrays(Container container, Array files, Array with) {
    ArrayBuilder builder = container.create().arrayBuilder(FILE);

    for (Value file : files) {
      builder.add(file);
    }
    for (Value file : with) {
      builder.add(file);
    }

    return builder.build();
  }
}
