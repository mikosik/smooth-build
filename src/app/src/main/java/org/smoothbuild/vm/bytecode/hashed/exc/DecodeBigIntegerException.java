package org.smoothbuild.vm.bytecode.hashed.exc;

import org.smoothbuild.common.Hash;

public class DecodeBigIntegerException extends HashedDbException {
  public DecodeBigIntegerException(Hash hash) {
    super("Value at " + hash + " cannot be decoded as BigInteger.");
  }
}
