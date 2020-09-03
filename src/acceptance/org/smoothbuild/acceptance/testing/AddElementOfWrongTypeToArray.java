package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.ArrayBuilder;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class AddElementOfWrongTypeToArray {
  @NativeImplementation("addElementOfWrongTypeToArray")
  public static Array addElementOfWrongTypeToArray(NativeApi nativeApi) {
    ArrayBuilder arrayBuilder = nativeApi.factory().arrayBuilder(nativeApi.factory().blobSpec());
    RString string = nativeApi.factory().string("abc");
    arrayBuilder.add(string);
    return arrayBuilder.build();
  }
}
