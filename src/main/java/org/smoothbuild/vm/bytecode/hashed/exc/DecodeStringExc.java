package org.smoothbuild.vm.bytecode.hashed.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeStringExc extends HashedDbExc {
  public DecodeStringExc(Hash hash, Throwable cause) {
    super("Value at " + hash + " cannot be decoded as string.", cause);
  }
}
