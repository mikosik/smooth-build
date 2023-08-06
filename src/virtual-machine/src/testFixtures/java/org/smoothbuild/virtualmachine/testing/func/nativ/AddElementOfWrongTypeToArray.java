package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class AddElementOfWrongTypeToArray {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    var factory = nativeApi.factory();
    var arrayBuilder = factory.arrayBuilderWithElems(factory.blobT());
    var string = factory.string("abc");
    arrayBuilder.add(string);
    return arrayBuilder.build();
  }
}
