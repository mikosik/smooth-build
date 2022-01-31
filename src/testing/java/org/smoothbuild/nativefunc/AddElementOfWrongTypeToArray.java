package org.smoothbuild.nativefunc;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.plugin.NativeApi;

public class AddElementOfWrongTypeToArray {
  public static ArrayB func(NativeApi nativeApi) {
    var factory = nativeApi.factory();
    var arrayBuilder = factory.arrayBuilderWithElems(factory.blobT());
    var string = factory.string("abc");
    arrayBuilder.add(string);
    return arrayBuilder.build();
  }
}
