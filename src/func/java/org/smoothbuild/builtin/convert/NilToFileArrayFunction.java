package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SFile;

public class NilToFileArrayFunction {
  @SmoothFunction
  public static Array<SFile> nilToFileArray( //
      Container container, //
      @Required @Name("input") Array<Nothing> input) {
    return container.arrayBuilder(SFile.class).build();
  }
}
