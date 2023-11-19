package org.smoothbuild.vm.bytecode.hashed.exc;

public class CorruptedHashedDbException extends HashedDbException {
  public CorruptedHashedDbException(String message) {
    super(message);
  }
}
