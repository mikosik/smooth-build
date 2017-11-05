package org.smoothbuild.builtin.convert;

import static org.smoothbuild.lang.type.Types.BLOB;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.Value;

public class FileArrayToBlobArrayFunction {
  @SmoothFunction
  public static Array fileArrayToBlobArray(Container container, Array files) {
    ArrayBuilder builder = container.create().arrayBuilder(BLOB);
    for (Value file : files) {
      builder.add(((SFile) file).content());
    }
    return builder.build();
  }
}
