package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BVariantType;

public class ChoiceHasIndexOutOfBoundException extends DecodeExprException {
  public ChoiceHasIndexOutOfBoundException(Hash hash, BVariantType type, int index, int size) {
    super(buildMessage(hash, type, index, size));
  }

  private static String buildMessage(Hash hash, BVariantType type, int index, int size) {
    return "Cannot decode %s expression at %s. Its index component is %s while Choice size is %s."
        .formatted(type.q(), hash, index, size);
  }
}
