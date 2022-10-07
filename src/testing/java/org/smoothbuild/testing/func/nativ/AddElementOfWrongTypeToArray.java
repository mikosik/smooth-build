package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class AddElementOfWrongTypeToArray {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    var factory = nativeApi.factory();
    var arrayBuilder = factory.arrayBuilderWithElems(factory.blobT());
    var string = factory.string("abc");
    arrayBuilder.add(string);
    return arrayBuilder.build();
  }
}
