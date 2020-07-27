package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.ArrayBuilder;
import org.smoothbuild.record.base.RString;

public class AddElementOfWrongTypeToArray {
  @SmoothFunction("addElementOfWrongTypeToArray")
  public static Array addElementOfWrongTypeToArray(NativeApi nativeApi) {
    ArrayBuilder arrayBuilder = nativeApi.factory().arrayBuilder(nativeApi.factory().blobSpec());
    RString string = nativeApi.factory().string("abc");
    arrayBuilder.add(string);
    return arrayBuilder.build();
  }
}
