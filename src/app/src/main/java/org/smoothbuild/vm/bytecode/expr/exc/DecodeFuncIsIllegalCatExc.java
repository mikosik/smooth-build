package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;

public class DecodeFuncIsIllegalCatExc extends DecodeExprExc {
  public DecodeFuncIsIllegalCatExc(Hash hash, FuncTB funcTB) {
    super("Cannot decode object at " + hash + ". Its category is " + funcTB.kind()
        + " which cannot have instances because it is abstract category.");
  }
}