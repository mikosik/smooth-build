package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class AddElementOfWrongTypeToArray {
  @SmoothFunction("addElementOfWrongTypeToArray")
  public static Array addElementOfWrongTypeToArray(NativeApi nativeApi) {
    ArrayBuilder arrayBuilder = nativeApi.create().arrayBuilder(nativeApi.types().blob());
    SString string = nativeApi.create().string("abc");
    arrayBuilder.add(string);
    return arrayBuilder.build();
  }
}
