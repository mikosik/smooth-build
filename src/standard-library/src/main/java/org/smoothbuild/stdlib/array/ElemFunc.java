package org.smoothbuild.stdlib.array;

import java.math.BigInteger;
import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

/**
 * A elem([A] array, Int index);
 */
public class ElemFunc {
  public static BValue bytecode(BytecodeFactory f, Map<String, BType> varMap)
      throws BytecodeException {
    var varA = varMap.get("A");
    var arrayParamType = f.arrayType(varA);
    var indexParamType = f.intType();
    var paramTypes = f.tupleType(arrayParamType, indexParamType);
    var arrayParamReference = f.reference(arrayParamType, f.int_(BigInteger.ZERO));
    var indexParamReference = f.reference(indexParamType, f.int_(BigInteger.ONE));
    var body = f.pick(arrayParamReference, indexParamReference);
    var funcType = f.lambdaType(paramTypes, varA);
    return f.lambda(funcType, body);
  }
}
