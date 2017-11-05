package org.smoothbuild.builtin.convert;

import static org.smoothbuild.lang.type.Types.FILE;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;

public class NilToFileArrayFunction {
  @SmoothFunction
  public static Array nilToFileArray(Container container, Array nil) {
    return container.create().arrayBuilder(FILE).build();
  }
}
