package org.smoothbuild.vm.bytecode.hashed.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeByteExc extends HashedDbExc {
  public DecodeByteExc(Hash hash) {
    super("Value at " + hash + " cannot be decoded as byte.");
  }
}

