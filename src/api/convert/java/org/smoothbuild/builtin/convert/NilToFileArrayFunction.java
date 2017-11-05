package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SFile;

public class NilToFileArrayFunction {
  @SmoothFunction
  public static Array<SFile> nilToFileArray(Container container, Array<Nothing> nil) {
    return container.create().arrayBuilder(SFile.class).build();
  }
}
