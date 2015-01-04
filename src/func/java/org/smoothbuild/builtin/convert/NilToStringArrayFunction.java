package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunctionLegacy;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SString;

public class NilToStringArrayFunction {
  public interface Parameters {
    @Required
    public Array<Nothing> input();
  }

  @SmoothFunctionLegacy
  public static Array<SString> nilToStringArray(NativeApi nativeApi, Parameters params) {
    return nativeApi.arrayBuilder(SString.class).build();
  }
}
