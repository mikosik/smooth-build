package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SString;

public class NilToStringArrayFunction {
  @SmoothFunction
  public static Array<SString> nilToStringArray(Container container, Array<Nothing> nil) {
    return container.create().arrayBuilder(SString.class).build();
  }
}
