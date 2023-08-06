package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;

public class DecodeIllegalCategoryException extends DecodeExprException {
  public DecodeIllegalCategoryException(Hash hash, FuncTB funcTB) {
    super("Cannot decode object at " + hash + ". Its category is " + funcTB.kind()
        + " which cannot have instances because it is abstract category.");
  }
}
