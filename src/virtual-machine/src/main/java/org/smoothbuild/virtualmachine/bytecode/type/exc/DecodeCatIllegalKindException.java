package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.Hash;

public class DecodeCatIllegalKindException extends DecodeCatException {
  public DecodeCatIllegalKindException(Hash hash, byte marker) {
    super("Cannot decode category at %s. It has illegal CategoryKind marker = %s."
        .formatted(hash, marker));
  }
}
