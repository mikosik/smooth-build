package org.smoothbuild.stdlib.core;

import java.math.BigInteger;
import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

/**
 * [R] map([S] array, S->R mapper)
 */
public class MapFunc {
  public static BValue bytecode(BytecodeFactory f, Map<String, BType> varMap)
      throws BytecodeException {
    var r = varMap.get("R");
    var s = varMap.get("S");

    var resultType = f.arrayType(r);
    var arrayParamType = f.arrayType(s);
    var mapperParamType = f.funcType(f.tupleType(s), r);
    var parameterTypes = f.tupleType(arrayParamType, mapperParamType);

    var arrayParamReference = f.reference(arrayParamType, f.int_(BigInteger.ZERO));
    var mapperParamReference = f.reference(mapperParamType, f.int_(BigInteger.ONE));

    var funcType = f.funcType(parameterTypes, resultType);
    var body = f.map(arrayParamReference, mapperParamReference);
    return f.lambda(funcType, body);
  }
}
