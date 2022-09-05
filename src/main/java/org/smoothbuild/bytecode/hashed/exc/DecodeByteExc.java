package org.smoothbuild.bytecode.hashed.exc;

import org.smoothbuild.bytecode.hashed.Hash;

public class DecodeByteExc extends HashedDbExc {
  public DecodeByteExc(Hash hash) {
    super("Value at " + hash + " cannot be decoded as byte.");
  }
}

