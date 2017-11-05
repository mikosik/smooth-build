package org.smoothbuild.acceptance.lang.nativ;

import static org.smoothbuild.lang.type.Types.STRING;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;

public class EmptyStringArray {
  @SmoothFunction
  public static Array emptyStringArray(Container container) {
    return container.create().arrayBuilder(STRING).build();
  }
}
