package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeSpecIllegalKindException extends DecodeSpecException {
  public DecodeSpecIllegalKindException(Hash hash, byte marker) {
    super("Cannot decode spec at %s. It has illegal SpecKind marker = %s."
        .formatted(hash, marker));
  }
}
