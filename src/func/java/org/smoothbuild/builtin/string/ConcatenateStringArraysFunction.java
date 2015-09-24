package org.smoothbuild.builtin.string;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SString;

public class ConcatenateStringArraysFunction {
  @SmoothFunction
  public static Array<SString> concatenateStringArrays( //
      Container container, //
      @Required @Name("strings") Array<SString> strings, //
      @Required @Name("with") Array<SString> with) {
    ArrayBuilder<SString> builder = container.create().arrayBuilder(SString.class);

    for (SString string : strings) {
      builder.add(string);
    }
    for (SString string : with) {
      builder.add(string);
    }

    return builder.build();
  }
}
