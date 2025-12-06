package org.smoothbuild.stdlib.core;

import static java.util.Objects.requireNonNull;

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
    var r = requireNonNull(varMap.get("R"));
    var s = requireNonNull(varMap.get("S"));

    var resultType = f.arrayType(r);
    var arrayParamType = f.arrayType(s);
    var mapperParamType = f.lambdaType(f.tupleType(s), r);
    var parameterTypes = f.tupleType(arrayParamType, mapperParamType);

    var arrayParamReference = f.paramRef(arrayParamType, f.int_(BigInteger.ZERO));
    var mapperParamReference = f.paramRef(mapperParamType, f.int_(BigInteger.ONE));

    var funcType = f.lambdaType(parameterTypes, resultType);
    var body = f.map(arrayParamReference, mapperParamReference);
    return f.lambda(funcType, body);
  }
}
