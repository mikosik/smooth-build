package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeCatIllegalKindExc extends DecodeCatExc {
  public DecodeCatIllegalKindExc(Hash hash, byte marker) {
    super("Cannot decode category at %s. It has illegal CategoryKind marker = %s."
        .formatted(hash, marker));
  }
}