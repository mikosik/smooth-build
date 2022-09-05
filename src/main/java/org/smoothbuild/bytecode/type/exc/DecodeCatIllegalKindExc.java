package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.hashed.Hash;

public class DecodeCatIllegalKindExc extends DecodeCatExc {
  public DecodeCatIllegalKindExc(Hash hash, byte marker) {
    super("Cannot decode category at %s. It has illegal CategoryKind marker = %s."
        .formatted(hash, marker));
  }
}
