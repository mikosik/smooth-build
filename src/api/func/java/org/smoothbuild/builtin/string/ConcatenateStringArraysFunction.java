package org.smoothbuild.builtin.string;

import static org.smoothbuild.lang.type.Types.STRING;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SString;

public class ConcatenateStringArraysFunction {
  @SmoothFunction
  public static Array concatenateStringArrays(NativeApi nativeApi, Array strings,
      Array with) {
    ArrayBuilder builder = nativeApi.create().arrayBuilder(STRING);

    for (SString string : strings.asIterable(SString.class)) {
      builder.add(string);
    }
    for (SString string : with.asIterable(SString.class)) {
      builder.add(string);
    }

    return builder.build();
  }
}
