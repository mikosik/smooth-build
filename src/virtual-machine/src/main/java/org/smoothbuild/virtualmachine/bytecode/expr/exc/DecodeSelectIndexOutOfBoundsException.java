package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.BKind;

public class DecodeSelectIndexOutOfBoundsException extends DecodeExprException {
  public DecodeSelectIndexOutOfBoundsException(Hash hash, BKind kind, int index, int size) {
    super(buildMessage(hash, kind, index, size));
  }

  private static String buildMessage(Hash hash, BKind kind, int index, int size) {
    return "Cannot decode %s expression at %s. Its index component is %s while TUPLE size is %s."
        .formatted(kind.q(), hash, index, size);
  }
}
