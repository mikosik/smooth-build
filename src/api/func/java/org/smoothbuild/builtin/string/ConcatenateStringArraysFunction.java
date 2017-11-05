package org.smoothbuild.builtin.string;

import static org.smoothbuild.lang.type.Types.STRING;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Value;

public class ConcatenateStringArraysFunction {
  @SmoothFunction
  public static Array concatenateStringArrays(Container container, Array strings,
      Array with) {
    ArrayBuilder builder = container.create().arrayBuilder(STRING);

    for (Value string : strings) {
      builder.add(string);
    }
    for (Value string : with) {
      builder.add(string);
    }

    return builder.build();
  }
}
