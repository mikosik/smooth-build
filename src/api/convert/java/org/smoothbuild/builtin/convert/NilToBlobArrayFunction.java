package org.smoothbuild.builtin.convert;

import static org.smoothbuild.lang.type.Types.BLOB;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;

public class NilToBlobArrayFunction {
  @SmoothFunction
  public static Array nilToBlobArray(Container container, Array nil) {
    return container.create().arrayBuilder(BLOB).build();
  }
}
