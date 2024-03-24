package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.base.Hash;

public class DecodeKindIllegalIdException extends DecodeKindException {
  public DecodeKindIllegalIdException(Hash hash, byte marker) {
    super("Cannot decode kind at %s. It has illegal kind id = %s.".formatted(hash, marker));
  }
}
