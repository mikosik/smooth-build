package org.smoothbuild.stdlib.core;

import java.math.BigInteger;
import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

/**
 * A fold([E] array, A initial, (A,E)->A folder)
 */
public class FoldFunc {
  public static BValue bytecode(BytecodeFactory f, Map<String, BType> varMap)
      throws BytecodeException {
    var a = varMap.get("A");
    var e = varMap.get("E");

    var resultType = a;
    var arrayParamType = f.arrayType(e);
    var initialParamType = a;
    var folderParamType = f.lambdaType(f.tupleType(a, e), a);
    var parameterTypes = f.tupleType(arrayParamType, initialParamType, folderParamType);

    var arrayParamReference = f.reference(arrayParamType, f.int_(BigInteger.ZERO));
    var initialParamReference = f.reference(initialParamType, f.int_(BigInteger.ONE));
    var folderParamReference = f.reference(folderParamType, f.int_(BigInteger.TWO));

    var funcType = f.lambdaType(parameterTypes, resultType);
    var body = f.fold(arrayParamReference, initialParamReference, folderParamReference);
    return f.lambda(funcType, body);
  }
}
