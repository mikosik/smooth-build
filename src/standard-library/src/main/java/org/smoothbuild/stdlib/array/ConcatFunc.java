package org.smoothbuild.stdlib.array;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ConcatFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BArray array = (BArray) args.get(0);
    var factory = nativeApi.factory();
    var elementType = ((BArrayType) array.evaluationType().element()).element();
    var resultBuilder = factory.arrayBuilderWithElements(elementType);
    var elements = array.elements(BArray.class);
    elements.foreach(innerArray -> resultBuilder.addAll(innerArray.elements(BValue.class)));
    return resultBuilder.build();
  }
}
