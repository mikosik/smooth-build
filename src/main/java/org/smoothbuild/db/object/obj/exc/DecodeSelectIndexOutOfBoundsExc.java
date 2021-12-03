package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatH;

public class DecodeSelectIndexOutOfBoundsExc extends DecodeObjExc {
  public DecodeSelectIndexOutOfBoundsExc(Hash hash, CatH cat, int index, int size) {
    super(buildMessage(hash, cat, index, size));
  }

  private static String buildMessage(Hash hash, CatH cat, int index, int size) {
    return "Cannot decode %s object at %s. Its index component is %s while TUPLE size is %s."
        .formatted(cat.q(), hash, index, size);
  }
}
