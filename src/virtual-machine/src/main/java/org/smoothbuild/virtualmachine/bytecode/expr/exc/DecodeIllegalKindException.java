package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BFuncType;

public class DecodeIllegalKindException extends DecodeExprException {
  public DecodeIllegalKindException(Hash hash, BFuncType funcType) {
    super("Cannot decode expression at " + hash + ". Its kind is " + funcType.name()
        + " which cannot have instances because it is abstract kind.");
  }
}
