package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;

public class DecodeIllegalCategoryException extends DecodeExprException {
  public DecodeIllegalCategoryException(Hash hash, FuncTB funcTB) {
    super("Cannot decode object at " + hash + ". Its category is " + funcTB.name()
        + " which cannot have instances because it is abstract category.");
  }
}
