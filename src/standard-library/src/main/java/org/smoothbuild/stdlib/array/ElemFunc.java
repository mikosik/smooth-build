package org.smoothbuild.stdlib.array;

import java.math.BigInteger;
import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * A elem([A] array, Int index);
 */
public class ElemFunc {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) throws BytecodeException {
    var varA = varMap.get("A");
    var arrayParamT = f.arrayT(varA);
    var indexParamT = f.intT();
    var paramTs = f.tupleT(arrayParamT, indexParamT);
    var arrayParamRef = f.reference(arrayParamT, BigInteger.ZERO);
    var indexParamRef = f.reference(indexParamT, BigInteger.ONE);
    var body = f.pick(arrayParamRef, indexParamRef);
    var funcT = f.funcT(paramTs, varA);
    return f.lambda(funcT, body);
  }
}
