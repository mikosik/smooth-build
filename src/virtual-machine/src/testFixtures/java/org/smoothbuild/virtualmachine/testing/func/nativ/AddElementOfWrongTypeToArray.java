package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class AddElementOfWrongTypeToArray {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    var factory = nativeApi.factory();
    var arrayBuilder = factory.arrayBuilderWithElements(factory.blobType());
    var string = factory.string("abc");
    arrayBuilder.add(string);
    return arrayBuilder.build();
  }
}
