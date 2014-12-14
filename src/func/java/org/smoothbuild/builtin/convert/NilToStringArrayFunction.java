package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.Nothing;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class NilToStringArrayFunction {
  public interface Parameters {
    @Required
    public Array<Nothing> input();
  }

  @SmoothFunction
  public static Array<SString> nilToStringArray(NativeApi nativeApi, Parameters params) {
    return nativeApi.arrayBuilder(SString.class).build();
  }
}
