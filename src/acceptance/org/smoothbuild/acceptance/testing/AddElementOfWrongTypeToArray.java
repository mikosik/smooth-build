package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.plugin.NativeApi;

public class AddElementOfWrongTypeToArray {
  public static Array function(NativeApi nativeApi) {
    ArrayBuilder arrayBuilder = nativeApi.factory().arrayBuilder(nativeApi.factory().blobType());
    Str string = nativeApi.factory().string("abc");
    arrayBuilder.add(string);
    return arrayBuilder.build();
  }
}
