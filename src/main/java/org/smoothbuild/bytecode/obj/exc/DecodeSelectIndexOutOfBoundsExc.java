package org.smoothbuild.bytecode.obj.exc;

import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.db.Hash;

public class DecodeSelectIndexOutOfBoundsExc extends DecodeObjExc {
  public DecodeSelectIndexOutOfBoundsExc(Hash hash, CatB cat, int index, int size) {
    super(buildMessage(hash, cat, index, size));
  }

  private static String buildMessage(Hash hash, CatB cat, int index, int size) {
    return "Cannot decode %s object at %s. Its index component is %s while TUPLE size is %s."
        .formatted(cat.q(), hash, index, size);
  }
}
