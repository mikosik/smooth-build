package org.smoothbuild.virtualmachine.bytecode.hashed.exc;

import org.smoothbuild.common.Hash;

public class DecodeBigIntegerException extends HashedDbException {
  public DecodeBigIntegerException(Hash hash) {
    super("Value at " + hash + " cannot be decoded as BigInteger.");
  }
}
