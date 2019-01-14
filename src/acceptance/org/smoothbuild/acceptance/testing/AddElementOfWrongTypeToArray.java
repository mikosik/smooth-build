package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SString;

public class AddElementOfWrongTypeToArray {
  @SmoothFunction("addElementOfWrongTypeToArray")
  public static Array addElementOfWrongTypeToArray(NativeApi nativeApi) {
    ArrayBuilder arrayBuilder = nativeApi.create().arrayBuilder(nativeApi.types().blob());
    SString string = nativeApi.create().string("abc");
    arrayBuilder.add(string);
    return arrayBuilder.build();
  }
}
