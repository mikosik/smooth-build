package org.smoothbuild.stdlib.core;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

/**
 * [A] filter_([A] array, [Bool] filter);
 */
public class FilterByMaskFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BArray array = (BArray) args.get(0);
    BArray masks = (BArray) args.get(1);

    var arrayBuilder = nativeApi.factory().arrayBuilder(array.type());
    var arrayIterator = array.elements(BValue.class).iterator();
    var maskIterator = masks.elements(BBool.class).iterator();
    while (arrayIterator.hasNext() && maskIterator.hasNext()) {
      var element = arrayIterator.next();
      if (maskIterator.next().toJavaBoolean()) {
        arrayBuilder.add(element);
      }
    }
    if (arrayIterator.hasNext()) {
      nativeApi.log().error("'array' has more elements than 'masks'.");
      return null;
    }
    if (maskIterator.hasNext()) {
      nativeApi.log().error("'array' has less elements than 'masks'.");
      return null;
    }
    return arrayBuilder.build();
  }
}
