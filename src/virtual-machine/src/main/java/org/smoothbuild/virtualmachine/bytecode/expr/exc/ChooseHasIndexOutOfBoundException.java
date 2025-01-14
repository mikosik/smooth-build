package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChoiceType;

public class ChooseHasIndexOutOfBoundException extends DecodeExprException {
  public ChooseHasIndexOutOfBoundException(Hash hash, BChoiceType type, int index, int size) {
    super(buildMessage(hash, type, index, size));
  }

  private static String buildMessage(Hash hash, BChoiceType type, int index, int size) {
    return "Cannot decode %s expression at %s. Its index component is %s while Choice size is %s."
        .formatted(type.q(), hash, index, size);
  }
}
