package org.smoothbuild.stdlib.core;

import java.math.BigInteger;
import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * A if(Bool condition, A then, A else)
 */
public class IfFunc {
  public static BValue bytecode(BytecodeFactory f, Map<String, BType> varMap)
      throws BytecodeException {
    var resultType = varMap.get("A");
    var conditionParamType = f.boolType();
    var parameterTypes = f.tupleType(conditionParamType, resultType, resultType);

    var conditionParamReference = f.reference(conditionParamType, f.int_(BigInteger.ZERO));
    var thenParamReference = f.reference(resultType, f.int_(BigInteger.ONE));
    var elseParamReference = f.reference(resultType, f.int_(BigInteger.TWO));

    var funcType = f.funcType(parameterTypes, resultType);
    var body = f.if_(conditionParamReference, thenParamReference, elseParamReference);
    return f.lambda(funcType, body);
  }
}
