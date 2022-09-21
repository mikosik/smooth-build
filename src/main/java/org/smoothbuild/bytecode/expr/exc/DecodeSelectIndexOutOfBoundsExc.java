package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CategoryB;

public class DecodeSelectIndexOutOfBoundsExc extends DecodeExprExc {
  public DecodeSelectIndexOutOfBoundsExc(Hash hash, CategoryB cat, int index, int size) {
    super(buildMessage(hash, cat, index, size));
  }

  private static String buildMessage(Hash hash, CategoryB cat, int index, int size) {
    return "Cannot decode %s object at %s. Its index component is %s while TUPLE size is %s."
        .formatted(cat.q(), hash, index, size);
  }
}
