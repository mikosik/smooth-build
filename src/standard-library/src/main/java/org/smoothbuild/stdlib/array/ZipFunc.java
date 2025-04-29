package org.smoothbuild.stdlib.array;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ZipFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BArray firstArray = (BArray) args.get(0);
    BArray secondArray = (BArray) args.get(1);

    var factory = nativeApi.factory();
    var firstElementType = firstArray.evaluationType().element();
    var secondElementType = secondArray.evaluationType().element();

    // Create a tuple type for the result elements
    var tupleType = factory.tupleType(firstElementType, secondElementType);

    // Create an array builder for the result
    var arrayType = factory.arrayType(tupleType);
    var resultBuilder = factory.arrayBuilder(arrayType);

    // Get the elements from both arrays
    var firstElements = firstArray.elements(BValue.class);
    var secondElements = secondArray.elements(BValue.class);

    // Determine the minimum size of the two arrays
    long minSize = Math.min(firstArray.size(), secondArray.size());

    // Zip the elements together
    for (int i = 0; i < minSize; i++) {
      BValue firstElement = firstElements.get(i);
      BValue secondElement = secondElements.get(i);

      // Create a tuple for each pair of elements
      BTuple tuple = factory.tuple(list(firstElement, secondElement));

      // Add the tuple to the result array
      resultBuilder.add(tuple);
    }

    return resultBuilder.build();
  }
}
