package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.Spec;

public class DecodeSelectIndexOutOfBoundsException extends DecodeObjException {
  public DecodeSelectIndexOutOfBoundsException(Hash hash, Spec spec, int index, int size) {
    super(buildMessage(hash, spec, index, size));
  }

  private static String buildMessage(Hash hash, Spec spec, int index, int size) {
    return "Cannot decode %s object at %s. Its index component is %s while RECORD size is %s."
        .formatted(spec.q(), hash, index, size);
  }
}
