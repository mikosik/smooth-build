package org.smoothbuild.builtin.string;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunctionLegacy;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SString;

public class ConcatenateStringsFunction {

  public interface ConcatenateStringsParameters {
    @Required
    public Array<SString> strings();

    @Required
    public Array<SString> with();
  }

  @SmoothFunctionLegacy
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
