package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryB;

public class DecodeSelectIndexOutOfBoundsException extends DecodeExprException {
  public DecodeSelectIndexOutOfBoundsException(Hash hash, CategoryB cat, int index, int size) {
    super(buildMessage(hash, cat, index, size));
  }

  private static String buildMessage(Hash hash, CategoryB cat, int index, int size) {
    return "Cannot decode %s object at %s. Its index component is %s while TUPLE size is %s."
        .formatted(cat.q(), hash, index, size);
  }
}
