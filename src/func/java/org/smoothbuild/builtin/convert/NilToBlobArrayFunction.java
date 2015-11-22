package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;

public class NilToBlobArrayFunction {
  @SmoothFunction
  public static Array<Blob> nilToBlobArray(
      Container container,
      @Required @Name("input") Array<Nothing> input) {
    return container.create().arrayBuilder(Blob.class).build();
  }
}
