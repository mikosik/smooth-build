package org.smoothbuild.slib.array;

import java.math.BigInteger;
import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.bytecode.type.inst.TypeB;

public class ElemFunc {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var varA = varMap.get("A");
    var arrayParamT = f.arrayT(varA);
    var indexParamT = f.intT();
    var paramTs = f.tupleT(arrayParamT, indexParamT);
    var arrayParamRef = f.ref(arrayParamT, BigInteger.ZERO);
    var indexParamRef = f.ref(indexParamT, BigInteger.ONE);
    var body = f.pick(arrayParamRef, indexParamRef);
    var funcT = f.funcT(paramTs, varA);
    return f.definedFunc(funcT, body);
  }
}
