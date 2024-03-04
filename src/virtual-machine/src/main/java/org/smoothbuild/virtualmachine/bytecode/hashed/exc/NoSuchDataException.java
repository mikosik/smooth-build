package org.smoothbuild.virtualmachine.bytecode.hashed.exc;

import org.smoothbuild.common.base.Hash;

public class NoSuchDataException extends HashedDbException {
  public NoSuchDataException(Hash hash) {
    super("No data at " + hash + ".");
  }
}
