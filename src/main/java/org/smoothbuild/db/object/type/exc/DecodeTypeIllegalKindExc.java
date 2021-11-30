package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeTypeIllegalKindExc extends DecodeTypeExc {
  public DecodeTypeIllegalKindExc(Hash hash, byte marker) {
    super("Cannot decode type at %s. It has illegal ObjKind marker = %s."
        .formatted(hash, marker));
  }
}
