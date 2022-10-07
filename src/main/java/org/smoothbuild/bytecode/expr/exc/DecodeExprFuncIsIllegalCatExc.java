package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.inst.FuncTB;

public class DecodeExprFuncIsIllegalCatExc extends DecodeExprExc {
  public DecodeExprFuncIsIllegalCatExc(Hash hash, FuncTB funcTB) {
    super("Cannot decode object at " + hash + ". Its category is " + funcTB.kind()
        + " which cannot have instances because it is abstract category.");
  }
}
