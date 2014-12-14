package org.smoothbuild.builtin.string;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ConcatenateStringsFunction {

  public interface ConcatenateStringsParameters {
    @Required
    public Array<SString> strings();

    @Required
    public Array<SString> with();
  }

  @SmoothFunction
  public static Array<SString> concatenateStrings(NativeApi nativeApi,
      ConcatenateStringsParameters params) {
    ArrayBuilder<SString> builder = nativeApi.arrayBuilder(SString.class);

    for (SString string : params.strings()) {
      builder.add(string);
    }
    for (SString string : params.with()) {
      builder.add(string);
    }

    return builder.build();
  }
}
