package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.base.Hash;

public class DecodeCatIllegalIdException extends DecodeCatException {
  public DecodeCatIllegalIdException(Hash hash, byte marker) {
    super("Cannot decode category at %s. It has illegal category id = %s.".formatted(hash, marker));
  }
}
