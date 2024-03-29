package org.smoothbuild.vm.bytecode.hashed.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeBigIntegerExc extends HashedDbExc {
  public DecodeBigIntegerExc(Hash hash) {
    super("Value at " + hash + " cannot be decoded as BigInteger.");
  }
}
