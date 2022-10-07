package org.smoothbuild.slib.array;

import java.math.BigInteger;
import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.type.inst.TypeB;

public class ElemFunc {
  public static InstB bytecode(BytecodeF bytecodeF, Map<String, TypeB> varMap) {
    var varA = varMap.get("A");
    var arrayParamT = bytecodeF.arrayT(varA);
    var indexParamT = bytecodeF.intT();
    var paramTs = bytecodeF.tupleT(arrayParamT, indexParamT);
    var arrayParamRef = bytecodeF.ref(arrayParamT, BigInteger.ZERO);
    var indexParamRef = bytecodeF.ref(indexParamT, BigInteger.ONE);
    var body = bytecodeF.pick(arrayParamRef, indexParamRef);
    return bytecodeF.defFunc(varA, paramTs, body);
  }
}
