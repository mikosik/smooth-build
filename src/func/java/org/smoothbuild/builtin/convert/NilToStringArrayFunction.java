package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SString;

public class NilToStringArrayFunction {
  @SmoothFunction
  public static Array<SString> nilToStringArray( //
      NativeApi nativeApi, //
      @Required @Name("input") Array<Nothing> input) {
    return nativeApi.arrayBuilder(SString.class).build();
  }
}
