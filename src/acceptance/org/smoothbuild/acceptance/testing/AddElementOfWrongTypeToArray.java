package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.plugin.NativeApi;

public class AddElementOfWrongTypeToArray {
  public static ArrayH function(NativeApi nativeApi) {
    ArrayHBuilder arrayBuilder = nativeApi.factory().arrayBuilder(nativeApi.factory().blobT());
    StringH string = nativeApi.factory().string("abc");
    arrayBuilder.add(string);
    return arrayBuilder.build();
  }
}
