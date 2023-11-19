package org.smoothbuild.vm.bytecode.hashed.exc;

import java.io.IOException;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class HashedDbException extends Exception {
  public HashedDbException(String message) {
    super(message);
  }

  public HashedDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public HashedDbException(Hash hash, IOException cause) {
    super("Cannot read data at " + hash + ".", cause);
  }

  public HashedDbException(Throwable cause) {
    super(cause);
  }
}
