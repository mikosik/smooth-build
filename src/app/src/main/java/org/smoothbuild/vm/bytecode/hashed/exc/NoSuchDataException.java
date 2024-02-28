package org.smoothbuild.vm.bytecode.hashed.exc;

import org.smoothbuild.common.Hash;

public class NoSuchDataException extends HashedDbException {
  public NoSuchDataException(Hash hash) {
    super("No data at " + hash + ".");
  }
}
