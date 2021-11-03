package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.ObjType;

public class DecodeSelectIndexOutOfBoundsException extends DecodeObjException {
  public DecodeSelectIndexOutOfBoundsException(Hash hash, ObjType type, int index, int size) {
    super(buildMessage(hash, type, index, size));
  }

  private static String buildMessage(Hash hash, ObjType type, int index, int size) {
    return "Cannot decode %s object at %s. Its index component is %s while TUPLE size is %s."
        .formatted(type.q(), hash, index, size);
  }
}
