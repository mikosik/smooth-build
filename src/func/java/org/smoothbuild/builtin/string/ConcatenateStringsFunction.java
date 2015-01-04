package org.smoothbuild.builtin.string;

import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SString;

public class ConcatenateStringsFunction {
  @SmoothFunction
  public static Array<SString> concatenateStrings( //
      NativeApi nativeApi, //
      @Required @Name("strings") Array<SString> strings, //
      @Required @Name("with") Array<SString> with) {
    ArrayBuilder<SString> builder = nativeApi.arrayBuilder(SString.class);

    for (SString string : strings) {
      builder.add(string);
    }
    for (SString string : with) {
      builder.add(string);
    }

    return builder.build();
  }
}
