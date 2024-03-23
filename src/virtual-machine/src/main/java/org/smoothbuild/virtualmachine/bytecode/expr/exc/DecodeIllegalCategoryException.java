package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;

public class DecodeIllegalCategoryException extends DecodeExprException {
  public DecodeIllegalCategoryException(Hash hash, BFuncType funcType) {
    super("Cannot decode object at " + hash + ". Its category is " + funcType.name()
        + " which cannot have instances because it is abstract category.");
  }
}
