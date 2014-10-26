package org.smoothbuild.builtin.convert;

import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class NilToStringArrayFunction {
  public interface Parameters {
    @Required
    public Array<SNothing> input();
  }

  @SmoothFunction
  public static Array<SString> nilToStringArray(NativeApi nativeApi, Parameters params) {
    return nativeApi.arrayBuilder(STRING_ARRAY).build();
  }
}
