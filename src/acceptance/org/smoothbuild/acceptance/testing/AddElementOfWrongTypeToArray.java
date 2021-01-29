package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;

public class AddElementOfWrongTypeToArray {
  public static Array function(NativeApi nativeApi) {
    ArrayBuilder arrayBuilder = nativeApi.factory().arrayBuilder(nativeApi.factory().blobSpec());
    Str string = nativeApi.factory().string("abc");
    arrayBuilder.add(string);
    return arrayBuilder.build();
  }
}
