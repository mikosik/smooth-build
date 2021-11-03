package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeTypeIllegalKindException extends DecodeTypeException {
  public DecodeTypeIllegalKindException(Hash hash, byte marker) {
    super("Cannot decode type at %s. It has illegal ObjKind marker = %s."
        .formatted(hash, marker));
  }
}
