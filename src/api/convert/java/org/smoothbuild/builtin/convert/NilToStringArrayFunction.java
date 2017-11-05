package org.smoothbuild.builtin.convert;

import static org.smoothbuild.lang.type.Types.STRING;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;

public class NilToStringArrayFunction {
  @SmoothFunction
  public static Array nilToStringArray(Container container, Array nil) {
    return container.create().arrayBuilder(STRING).build();
  }
}
